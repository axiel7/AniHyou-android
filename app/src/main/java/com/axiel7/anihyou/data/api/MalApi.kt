package com.axiel7.anihyou.data.api

import com.axiel7.anihyou.data.model.media.AnimeThemes
import com.axiel7.anihyou.utils.MAL_API_URL
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MalApi @Inject constructor(
    private val client: OkHttpClient
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getAnimeThemes(id: Int): AnimeThemes? {
        val fields = "opening_themes,ending_themes"
        val request = Request.Builder()
            .url("${MAL_API_URL}anime/$id?fields=$fields")
            .build()
        return try {
            client.newCall(request).execute().body?.string()?.let {
                json.decodeFromString(it)
            }
        } catch (_: Exception) {
            null
        }
    }
}