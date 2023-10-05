package com.axiel7.anihyou.data.repository

import android.net.Uri
import androidx.work.WorkManager
import com.apollographql.apollo3.ApolloClient
import com.axiel7.anihyou.ViewerIdQuery
import com.axiel7.anihyou.worker.NotificationWorker.Companion.cancelNotificationWork
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val client: ApolloClient,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    private val workManager: WorkManager,
) {

    // login
    suspend fun parseRedirectUri(uri: Uri) {
        val dummyUrl = Uri.parse("http://dummyurl.com?${uri.fragment}")
        dummyUrl.getQueryParameter("access_token")?.let { token ->
            defaultPreferencesRepository.setAccessToken(token)
            refreshUserIdAndOptions()
        }
    }

    private suspend fun refreshUserIdAndOptions() {
        val response = client.query(ViewerIdQuery()).execute()
        if (!response.hasErrors()) {
            response.data?.Viewer?.let { viewer ->
                defaultPreferencesRepository.saveViewerInfo(viewer)
            }
        }
    }

    suspend fun logOut() {
        defaultPreferencesRepository.removeViewerInfo()
        workManager.cancelNotificationWork()
    }
}