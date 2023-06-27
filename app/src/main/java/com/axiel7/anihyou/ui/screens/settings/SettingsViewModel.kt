package com.axiel7.anihyou.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.ui.base.UiState
import kotlinx.coroutines.launch

class SettingsViewModel : BaseViewModel() {

    var displayAdultContent by mutableStateOf(false)
    private set

    fun onDisplayAdultContentChanged(value: Boolean) {
        displayAdultContent = value
    }

    suspend fun getUserOptions() = viewModelScope.launch {
        UserRepository.getUserOptions().collect { uiState ->
            isLoading = uiState is UiState.Loading

            if (uiState is UiState.Success) {
                displayAdultContent = uiState.data.displayAdultContent ?: false
            }
            else if (uiState is UiState.Error) {
                message = uiState.message
            }
        }
    }

    suspend fun updateUser() = viewModelScope.launch {
        UserRepository.updateUser(
            displayAdultContent = displayAdultContent
        ).collect { uiState ->
            isLoading = uiState is UiState.Loading

            if (uiState is UiState.Success) {
                displayAdultContent = uiState.data.options?.displayAdultContent ?: false
            }
            else if (uiState is UiState.Error) {
                message = uiState.message
            }
        }
    }
}