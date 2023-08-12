package com.axiel7.anihyou.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.fragment.UserOptionsFragment
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.type.UserStaffNameLanguage
import com.axiel7.anihyou.type.UserTitleLanguage
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class SettingsViewModel : BaseViewModel() {

    var userOptions by mutableStateOf<UserOptionsFragment?>(null)
        private set

    fun onDisplayAdultContentChanged(value: Boolean) {
        userOptions = userOptions?.copy(
            options = userOptions?.options?.copy(
                displayAdultContent = value
            )
        )
        updateUser(displayAdultContent = value)
    }

    fun onTitleLanguageChanged(value: UserTitleLanguage) {
        userOptions = userOptions?.copy(
            options = userOptions?.options?.copy(
                titleLanguage = value
            )
        )
        updateUser(titleLanguage = value)
    }

    fun onStaffNameLanguageChanged(value: UserStaffNameLanguage) {
        userOptions = userOptions?.copy(
            options = userOptions?.options?.copy(
                staffNameLanguage = value
            )
        )
        updateUser(staffNameLanguage = value)
    }

    fun onScoreFormatChanged(value: ScoreFormat) {
        userOptions = userOptions?.copy(
            mediaListOptions = userOptions?.mediaListOptions?.copy(
                scoreFormat = value
            )
        )
        updateUser(scoreFormat = value)
    }

    fun getUserOptions() = viewModelScope.launch(dispatcher) {
        UserRepository.getUserOptions().collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                userOptions = result.data
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    private fun updateUser(
        displayAdultContent: Boolean? = null,
        titleLanguage: UserTitleLanguage? = null,
        staffNameLanguage: UserStaffNameLanguage? = null,
        scoreFormat: ScoreFormat? = null,
    ) = viewModelScope.launch(dispatcher) {
        UserRepository.updateUser(
            displayAdultContent = displayAdultContent,
            titleLanguage = titleLanguage,
            staffNameLanguage = staffNameLanguage,
            scoreFormat = scoreFormat,
        ).collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                userOptions = result.data
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }
}