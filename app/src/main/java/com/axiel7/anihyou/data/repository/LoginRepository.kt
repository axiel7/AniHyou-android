package com.axiel7.anihyou.data.repository

import android.net.Uri
import androidx.datastore.preferences.core.edit
import com.axiel7.anihyou.App
import com.axiel7.anihyou.ViewerIdQuery
import com.axiel7.anihyou.data.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.NOTIFICATIONS_ENABLED_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.PROFILE_COLOR_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.USER_ID_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
import com.axiel7.anihyou.data.model.notification.NotificationInterval
import com.axiel7.anihyou.network.apolloClient
import com.axiel7.anihyou.worker.NotificationWorker

object LoginRepository {

    fun getUserId() = App.dataStore.getValueSync(USER_ID_PREFERENCE_KEY)

    suspend fun parseRedirectUri(uri: Uri) {
        val dummyUrl = Uri.parse("http://dummyurl.com?${uri.fragment}")
        dummyUrl.getQueryParameter("access_token")?.let { token ->
            App.accessToken = token
            App.dataStore.edit {
                it[ACCESS_TOKEN_PREFERENCE_KEY] = token
                // enable notifications by default when logging in
                it[NOTIFICATIONS_ENABLED_PREFERENCE_KEY] = true
            }
            NotificationWorker.scheduleNotificationWork(interval = NotificationInterval.DAILY)
            refreshUserIdAndOptions()
        }
    }

    private suspend fun refreshUserIdAndOptions() {
        val response = apolloClient.query(ViewerIdQuery()).execute()
        if (!response.hasErrors()) {
            response.data?.Viewer?.let { viewer ->
                App.dataStore.edit {
                    it[USER_ID_PREFERENCE_KEY] = viewer.id
                    it[PROFILE_COLOR_PREFERENCE_KEY] = viewer.options?.profileColor ?: "#526CFD"
                    it[SCORE_FORMAT_PREFERENCE_KEY] =
                        viewer.mediaListOptions?.scoreFormat?.name ?: "POINT_10"
                }
            }
        }
    }

    suspend fun removeUserInfo() {
        App.dataStore.edit {
            it.remove(ACCESS_TOKEN_PREFERENCE_KEY)
            it.remove(USER_ID_PREFERENCE_KEY)
            it.remove(PROFILE_COLOR_PREFERENCE_KEY)
            it.remove(SCORE_FORMAT_PREFERENCE_KEY)
            it.remove(NOTIFICATIONS_ENABLED_PREFERENCE_KEY)
        }
        App.accessToken = null
        NotificationWorker.cancelNotificationWork()
    }
}