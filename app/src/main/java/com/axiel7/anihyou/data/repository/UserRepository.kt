package com.axiel7.anihyou.data.repository

import com.axiel7.anihyou.App
import com.axiel7.anihyou.UnreadNotificationCountQuery
import com.axiel7.anihyou.data.PreferencesDataStore
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
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
}