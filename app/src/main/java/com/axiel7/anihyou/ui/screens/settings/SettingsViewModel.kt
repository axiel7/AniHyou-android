package com.axiel7.anihyou.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class SettingsViewModel : BaseViewModel() {

    var displayAdultContent by mutableStateOf(false)
        private set

    fun onDisplayAdultContentChanged(value: Boolean) {
        displayAdultContent = value
    }

    fun getUserOptions() = viewModelScope.launch(dispatcher) {
        UserRepository.getUserOptions().collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                displayAdultContent = result.data.displayAdultContent ?: false
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    fun updateUser() = viewModelScope.launch(dispatcher) {
        UserRepository.updateUser(
            displayAdultContent = displayAdultContent
        ).collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                displayAdultContent = result.data.options?.displayAdultContent ?: false
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }
}