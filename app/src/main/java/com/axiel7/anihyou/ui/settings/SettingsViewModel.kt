package com.axiel7.anihyou.ui.settings

import androidx.compose.runtime.mutableStateOf
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.UpdateUserMutation
import com.axiel7.anihyou.UserOptionsQuery
import com.axiel7.anihyou.ui.base.BaseViewModel

class SettingsViewModel : BaseViewModel() {

    var displayAdultContent = mutableStateOf(false)

    suspend fun getUserOptions() {
        isLoading = true
        val response = UserOptionsQuery().tryQuery()

        response?.data?.Viewer?.options?.let {
            displayAdultContent.value = it.displayAdultContent ?: false
        }
        isLoading = false
    }

    suspend fun updateUser() {
        isLoading = true
        val response = UpdateUserMutation(
            displayAdultContent = Optional.present(displayAdultContent.value)
        ).tryMutation()

        response?.data?.UpdateUser?.options?.let {
            displayAdultContent.value = it.displayAdultContent ?: false
        }
        isLoading = false
    }
}