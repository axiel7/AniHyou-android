package com.axiel7.anihyou.feature.settings

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.ListPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.LoginRepository
import com.axiel7.anihyou.core.domain.repository.UserRepository
import com.axiel7.anihyou.core.model.AppColorMode
import com.axiel7.anihyou.core.model.DefaultTab
import com.axiel7.anihyou.core.model.ItemsPerRow
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.model.Theme
import com.axiel7.anihyou.core.model.notification.NotificationInterval
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.network.type.UserStaffNameLanguage
import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.feature.worker.NotificationWorker.Companion.cancelNotificationWork
import com.axiel7.anihyou.feature.worker.NotificationWorker.Companion.scheduleNotificationWork
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModel(
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    private val listPreferencesRepository: ListPreferencesRepository,
    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository,
    private val workManager: WorkManager,
) : UiStateViewModel<SettingsUiState>(), SettingsEvent {

    override val initialState = SettingsUiState()

    private val profileColor = defaultPreferencesRepository.profileColor

    private val isLoggedIn = defaultPreferencesRepository.isLoggedIn

    override fun setTheme(value: Theme) {
        viewModelScope.launch {
            defaultPreferencesRepository.setTheme(value)
        }
    }

    override fun setUseBlackColors(value: Boolean) {
        viewModelScope.launch {
            defaultPreferencesRepository.setUseBlackColors(value)
        }
    }

    override fun setAppColorMode(value: AppColorMode) {
        viewModelScope.launch {
            defaultPreferencesRepository.setAppColorMode(value)
            when (value) {
                AppColorMode.DEFAULT -> setAppColor(null)
                AppColorMode.PROFILE -> {
                    profileColor.firstOrNull()?.let { setAppColor(it) }
                }
                AppColorMode.CUSTOM -> {}
            }
        }
    }

    override fun setCustomAppColor(color: Color) {
        viewModelScope.launch {
            defaultPreferencesRepository.setAppColor(color)
        }
    }

    private fun setAppColor(value: Color?) = viewModelScope.launch {
        defaultPreferencesRepository.setAppColor(value)
    }

    override fun setColorPalette(value: String) {
        viewModelScope.launch {
            defaultPreferencesRepository.setColorPalette(value)
        }
    }

    override fun setUseGeneralListStyle(value: Boolean) {
        viewModelScope.launch {
            listPreferencesRepository.setUseGeneralListStyle(value)
        }
    }

    override fun setGeneralListStyle(value: ListStyle) {
        viewModelScope.launch {
            listPreferencesRepository.setGeneralListStyle(value)
        }
    }

    override fun setGridItemsPerRow(value: ItemsPerRow) {
        viewModelScope.launch {
            listPreferencesRepository.setGridItemsPerRow(value)
        }
    }

    override fun setAiringOnMyList(value: Boolean) {
        viewModelScope.launch {
            defaultPreferencesRepository.setAiringOnMyList(value)
        }
    }

    // Notifications
    @OptIn(ExperimentalPermissionsApi::class)
    override fun setNotificationsEnabled(
        isEnabled: Boolean,
        notificationPermission: PermissionState?,
        createNotificationChannels: () -> Unit,
    ) {
        viewModelScope.launch {
            if (isEnabled) {
                if (notificationPermission == null || notificationPermission.status.isGranted) {
                    defaultPreferencesRepository.setNotificationsEnabled(true)
                    createNotificationChannels()
                    scheduleNotificationWork(interval = uiState.value.notificationCheckInterval)
                } else {
                    notificationPermission.launchPermissionRequest()
                }
            } else {
                defaultPreferencesRepository.setNotificationsEnabled(false)
                workManager.cancelNotificationWork()
            }
        }
    }

    override fun setNotificationCheckInterval(value: NotificationInterval) {
        viewModelScope.launch {
            defaultPreferencesRepository.setNotificationCheckInterval(value)
            scheduleNotificationWork(value)
        }
    }

    private fun scheduleNotificationWork(interval: NotificationInterval) {
        workManager.scheduleNotificationWork(interval)
    }

    override fun setDisplayAdultContent(value: Boolean) {
        viewModelScope.launch {
            defaultPreferencesRepository.setDisplayAdult(value)
            updateUser(displayAdultContent = value).collect()
        }
    }

    override fun setTitleLanguage(value: UserTitleLanguage) {
        viewModelScope.launch {
            updateUser(titleLanguage = value).collect()
        }
    }

    override fun setStaffNameLanguage(value: UserStaffNameLanguage) {
        viewModelScope.launch {
            updateUser(staffNameLanguage = value).collect()
        }
    }

    override fun setScoreFormat(value: ScoreFormat) {
        viewModelScope.launch {
            defaultPreferencesRepository.setScoreFormat(value)
            updateUser(scoreFormat = value).collect()
        }
    }

    override fun setDefaultTab(value: DefaultTab) {
        viewModelScope.launch {
            defaultPreferencesRepository.setDefaultTab(value)
        }
    }

    override fun setAiringNotification(value: Boolean) {
        viewModelScope.launch {
            updateUser(airingNotifications = value).collect()
        }
    }

    override fun logOut(recreate: () -> Unit) {
        viewModelScope.launch {
            loginRepository.logOut()
            workManager.cancelNotificationWork()
            recreate()
        }
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
                        userSettings = result.data
                    )
                } else {
                    result.toUiState()
                }
            }
        }

    init {
        isLoggedIn
            .onEach { value ->
                mutableUiState.update { it.copy(isLoggedIn = value) }
            }
            .filter { it }
            .flatMapLatest {
                userRepository.getViewerSettings()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            isLoading = false,
                            userSettings = result.data
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

        defaultPreferencesRepository.useBlackColors
            .onEach { value ->
                mutableUiState.update { it.copy(useBlackColors = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.appColorMode
            .onEach { value ->
                mutableUiState.update { it.copy(appColorMode = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.appColor
            .onEach { value ->
                mutableUiState.update { it.copy(appColor = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.colorPalette
            .filterNotNull()
            .onEach { value ->
                mutableUiState.update { it.copy(colorPaletteStyle = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.defaultTab
            .filterNotNull()
            .onEach { value ->
                mutableUiState.update { it.copy(defaultTab = value) }
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

        defaultPreferencesRepository.notificationCheckInterval
            .onEach { value ->
                mutableUiState.update { it.copy(notificationCheckInterval = value) }
            }
            .launchIn(viewModelScope)
    }
}