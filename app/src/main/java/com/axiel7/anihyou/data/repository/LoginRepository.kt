package com.axiel7.anihyou.data.repository

import android.net.Uri
import androidx.datastore.preferences.core.edit
import com.axiel7.anihyou.App
import com.axiel7.anihyou.CLIENT_ID
import com.axiel7.anihyou.data.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
import com.axiel7.anihyou.utils.ANILIST_AUTH_URL

object LoginRepository {

    fun getAccessToken() = App.dataStore.getValueSync(ACCESS_TOKEN_PREFERENCE_KEY)

    fun getAuthUrl() = "${ANILIST_AUTH_URL}?client_id=${CLIENT_ID}&response_type=token"

    suspend fun parseRedirectUri(uri: Uri) {
        val dummyUrl = Uri.parse("http://dummyurl.com?${uri.fragment}")
        dummyUrl.getQueryParameter("access_token")?.let { token ->
            App.dataStore.edit {
                it[ACCESS_TOKEN_PREFERENCE_KEY] = token
            }
        }
    }
}