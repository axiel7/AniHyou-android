package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.App
import com.axiel7.anihyou.UnreadNotificationCountQuery
import com.axiel7.anihyou.UpdateUserMutation
import com.axiel7.anihyou.UserOptionsQuery
import com.axiel7.anihyou.data.PreferencesDataStore
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryMutation
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import com.axiel7.anihyou.ui.base.UiState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

object UserRepository {

    fun getUnreadNotificationCount() = flow {
        val accessToken = App.dataStore.data.first()[PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY]
        if (accessToken != null) {
            val response = UnreadNotificationCountQuery().tryQuery()
            emit(response?.data?.Viewer?.unreadNotificationCount ?: 0)
        }
        else emit(0)
    }

    fun getUserOptions() = flow {
        emit(UiState.Loading)

        val response = UserOptionsQuery().tryQuery()

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val options = response?.data?.Viewer?.options
            if (options != null) emit(UiState.Success(data = options))
            else emit(UiState.Error(message = "Error"))
        }
    }

    fun updateUser(
        displayAdultContent: Boolean? = null
    ) = flow {
        emit(UiState.Loading)
        val response = UpdateUserMutation(
            displayAdultContent = Optional.presentIfNotNull(displayAdultContent)
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val user = response?.data?.UpdateUser
            if (user != null) emit(UiState.Success(data = user))
            else emit(UiState.Error(message = "Error"))
        }
    }
}