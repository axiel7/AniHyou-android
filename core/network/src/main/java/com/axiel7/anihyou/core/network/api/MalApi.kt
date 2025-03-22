package com.axiel7.anihyou.core.network.api

import com.axiel7.anihyou.core.base.MAL_API_URL
import com.axiel7.anihyou.core.network.api.model.AnimeThemesDto
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class MalApi (
    private val client: OkHttpClient
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun getAnimeThemes(id: Int): AnimeThemesDto? {
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