package com.axiel7.anihyou.ui.screens.settings

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.notification.NotificationInterval
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.ListPreferencesRepository
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.type.UserStaffNameLanguage
import com.axiel7.anihyou.type.UserTitleLanguage
import com.axiel7.anihyou.ui.common.AppColorMode
import com.axiel7.anihyou.ui.common.ItemsPerRow
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.Theme
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.ui.screens.home.HomeTab
import com.axiel7.anihyou.worker.NotificationWorker.Companion.cancelNotificationWork
import com.axiel7.anihyou.worker.NotificationWorker.Companion.scheduleNotificationWork
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    private val listPreferencesRepository: ListPreferencesRepository,
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository,
    private val workManager: WorkManager,
) : UiStateViewModel<SettingsUiState>() {

    override val mutableUiState = MutableStateFlow(SettingsUiState())
    override val uiState = mutableUiState.asStateFlow()

    val profileColor = defaultPreferencesRepository.profileColor.stateInViewModel()

    val notificationCheckInterval =
        defaultPreferencesRepository.notificationCheckInterval.stateInViewModel(
            initialValue = NotificationInterval.DAILY
        )

    fun setTheme(value: Theme) = viewModelScope.launch {
        defaultPreferencesRepository.setTheme(value)
    }

    fun setAppColorMode(value: AppColorMode) = viewModelScope.launch {
        defaultPreferencesRepository.setAppColorMode(value)
        when (value) {
            AppColorMode.DEFAULT -> setAppColor(null)
            AppColorMode.PROFILE -> {
                profileColor.firstOrNull()?.let { setAppColor(it) }
            }
        }
    }

    private fun setAppColor(value: Color?) = viewModelScope.launch {
        defaultPreferencesRepository.setAppColor(value)
    }

    fun setUseGeneralListStyle(value: Boolean) = viewModelScope.launch {
        listPreferencesRepository.setUseGeneralListStyle(value)
    }

    fun setGeneralListStyle(value: ListStyle) = viewModelScope.launch {
        listPreferencesRepository.setGeneralListStyle(value)
    }

    fun setGridItemsPerRow(value: ItemsPerRow) = viewModelScope.launch {
        listPreferencesRepository.setGridItemsPerRow(value)
    }

    fun setDefaultHomeTab(value: HomeTab) = viewModelScope.launch {
        defaultPreferencesRepository.setDefaultHomeTab(value)
    }

    fun setAiringOnMyList(value: Boolean) = viewModelScope.launch {
        defaultPreferencesRepository.setAiringOnMyList(value)
    }

    // Notifications
    @OptIn(ExperimentalPermissionsApi::class)
    fun setNotificationsEnabled(
        isEnabled: Boolean,
        notificationPermission: PermissionState?,
        createNotificationChannels: () -> Unit,
    ) = viewModelScope.launch {
        if (isEnabled) {
            if (notificationPermission == null || notificationPermission.status.isGranted) {
                defaultPreferencesRepository.setNotificationsEnabled(true)
                createNotificationChannels()
                scheduleNotificationWork(interval = notificationCheckInterval.first())
            } else {
                notificationPermission.launchPermissionRequest()
            }
        } else {
            defaultPreferencesRepository.setNotificationsEnabled(false)
            workManager.cancelNotificationWork()
        }
    }

    fun setNotificationCheckInterval(value: NotificationInterval) = viewModelScope.launch {
        defaultPreferencesRepository.setNotificationCheckInterval(value)
        scheduleNotificationWork(value)
    }

    private fun scheduleNotificationWork(interval: NotificationInterval) {
        workManager.scheduleNotificationWork(interval)
    }


    fun setDisplayAdultContent(value: Boolean) = viewModelScope.launch {
        updateUser(displayAdultContent = value).collect()
    }

    fun setTitleLanguage(value: UserTitleLanguage) = viewModelScope.launch {
        updateUser(titleLanguage = value).collect()
    }

    fun setStaffNameLanguage(value: UserStaffNameLanguage) = viewModelScope.launch {
        updateUser(staffNameLanguage = value).collect()
    }

    fun setScoreFormat(value: ScoreFormat) = viewModelScope.launch {
        defaultPreferencesRepository.setScoreFormat(value)
        updateUser(scoreFormat = value).collect()
    }

    fun setAiringNotification(value: Boolean) = viewModelScope.launch {
        updateUser(airingNotifications = value).collect()
    }

    private fun updateUser(
        displayAdultContent: Boolean? = null,
        titleLanguage: UserTitleLanguage? = null,
        staffNameLanguage: UserStaffNameLanguage? = null,
        scoreFormat: ScoreFormat? = null,
        airingNotifications: Boolean? = null,
    ) = userRepository
        .updateUser(
            displayAdultContent = displayAdultContent,
            titleLanguage = titleLanguage,
            staffNameLanguage = staffNameLanguage,
            scoreFormat = scoreFormat,
            airingNotifications = airingNotifications,
        )
        .onEach { result ->
            mutableUiState.update {
                if (result is DataResult.Success) {
                    it.copy(
                        isLoading = false,
                        userOptions = result.data
                    )
                } else {
                    result.toUiState()
                }
            }
        }

    fun logOut(recreate: () -> Unit) = viewModelScope.launch {
        loginRepository.logOut()
        recreate()
    }

    init {
        userRepository.getUserOptions()
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            isLoading = false,
                            userOptions = result.data
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.theme
            .onEach { value ->
                mutableUiState.update { it.copy(theme = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.appColorMode
            .onEach { value ->
                mutableUiState.update { it.copy(appColorMode = value) }
            }
            .launchIn(viewModelScope)

        listPreferencesRepository.useGeneralListStyle
            .onEach { value ->
                mutableUiState.update { it.copy(useGeneralListStyle = value) }
            }
            .launchIn(viewModelScope)

        listPreferencesRepository.generalListStyle
            .onEach { value ->
                mutableUiState.update { it.copy(generalListStyle = value) }
            }
            .launchIn(viewModelScope)

        listPreferencesRepository.gridItemsPerRow
            .onEach { value ->
                mutableUiState.update { it.copy(gridItemsPerRow = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.defaultHomeTab
            .onEach { value ->
                mutableUiState.update { it.copy(defaultHomeTab = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.airingOnMyList
            .onEach { value ->
                mutableUiState.update { it.copy(airingOnMyList = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.scoreFormat
            .onEach { value ->
                mutableUiState.update { it.copy(scoreFormat = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.isNotificationsEnabled
            .onEach { value ->
                mutableUiState.update { it.copy(isNotificationsEnabled = value) }
            }
            .launchIn(viewModelScope)
    }
}