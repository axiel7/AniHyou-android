package com.axiel7.anihyou.core.domain.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.apollographql.apollo.ApolloClient
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.axiel7.anihyou.core.network.ViewerOptionsQuery
import com.axiel7.anihyou.core.network.api.response.errorString

class LoginRepository (
    private val client: ApolloClient,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) {

    // login
    suspend fun parseRedirectUri(uri: Uri) {
        val dummyUrl = "http://dummyurl.com?${uri.fragment}".toUri()
        dummyUrl.getQueryParameter("access_token")?.let { token ->
            onNewToken(token)
        }
    }

    suspend fun onNewToken(token: String) {
        defaultPreferencesRepository.setAccessToken(token)
        refreshUserIdAndOptions()
    }

    private suspend fun refreshUserIdAndOptions() {
        val response = client.query(ViewerOptionsQuery())
            .fetchPolicy(FetchPolicy.NetworkOnly)
            .execute()
        if (response.data != null) {
            response.data?.Viewer?.let { viewer ->
                defaultPreferencesRepository.saveViewerInfo(viewer)
            }
        } else {
            Log.e("AniHyou", "Error saving user data: ${response.errorString}")
        }
    }

    suspend fun logOut() {
        defaultPreferencesRepository.removeViewerInfo()
    }
}