package com.axiel7.anihyou.core.network.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

private const val TMDB_BASE = "https://api.themoviedb.org/3"
const val TMDB_IMAGE_BASE = "https://image.tmdb.org/t/p/w300"

data class TmdbSeason(
    val number: Int,
    val name: String,
    val episodeCount: Int,
    val posterPath: String?,
)

data class TmdbEpisode(
    val number: Int,
    val name: String,
    val overview: String,
    val stillPath: String?,
    val rating: Double?,
    val airDate: String?,
)

/**
 * TMDB v3 REST API — episode metadata, seasons, thumbnails, ratings.
 * Requires a v3 API key configured in Settings → APIs → TMDB.
 */
class TmdbApi(
    private val okHttpClient: OkHttpClient,
    private val keyProvider: suspend () -> String?,
) {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    /** Search for a TV show by title. Returns the TMDB show ID, or null if not found. */
    suspend fun findShowId(query: String): String? = withContext(Dispatchers.IO) {
        runCatching {
            val key = keyProvider() ?: return@runCatching null
            val url = "$TMDB_BASE/search/tv?api_key=$key&query=${query.encodeUrl()}&page=1"
            val resp = okHttpClient.newCall(Request.Builder().url(url).build()).execute()
            val body = resp.body?.string() ?: return@runCatching null
            val results = json.parseToJsonElement(body).jsonObject["results"]?.jsonArray
            if (results.isNullOrEmpty()) return@runCatching null
            results[0].jsonObject["id"]?.jsonPrimitive?.int?.toString()
        }.getOrNull()
    }

    /** Fetch the list of seasons for a show (excludes season 0 / specials). */
    suspend fun getSeasons(tmdbId: String): List<TmdbSeason> = withContext(Dispatchers.IO) {
        runCatching {
            val key = keyProvider() ?: return@runCatching emptyList()
            val url = "$TMDB_BASE/tv/$tmdbId?api_key=$key"
            val resp = okHttpClient.newCall(Request.Builder().url(url).build()).execute()
            val body = resp.body?.string() ?: return@runCatching emptyList()
            val rawSeasons = json.parseToJsonElement(body).jsonObject["seasons"]?.jsonArray
                ?: return@runCatching emptyList()
            rawSeasons.mapNotNull { s ->
                val o = s.jsonObject
                val num = o["season_number"]?.jsonPrimitive?.int ?: return@mapNotNull null
                if (num == 0) return@mapNotNull null
                TmdbSeason(
                    number = num,
                    name = o["name"]?.jsonPrimitive?.content ?: "Season $num",
                    episodeCount = o["episode_count"]?.jsonPrimitive?.int ?: 0,
                    posterPath = o["poster_path"]?.jsonPrimitive?.content,
                )
            }
        }.getOrElse { emptyList() }
    }

    /** Fetch episodes for a specific season. */
    suspend fun getEpisodes(tmdbId: String, season: Int): List<TmdbEpisode> = withContext(Dispatchers.IO) {
        runCatching {
            val key = keyProvider() ?: return@runCatching emptyList()
            val url = "$TMDB_BASE/tv/$tmdbId/season/$season?api_key=$key"
            val resp = okHttpClient.newCall(Request.Builder().url(url).build()).execute()
            val body = resp.body?.string() ?: return@runCatching emptyList()
            val eps = json.parseToJsonElement(body).jsonObject["episodes"]?.jsonArray
                ?: return@runCatching emptyList()
            eps.map { e ->
                val o = e.jsonObject
                TmdbEpisode(
                    number = o["episode_number"]?.jsonPrimitive?.int ?: 0,
                    name = o["name"]?.jsonPrimitive?.content ?: "",
                    overview = o["overview"]?.jsonPrimitive?.content ?: "",
                    stillPath = o["still_path"]?.jsonPrimitive?.content,
                    rating = runCatching { o["vote_average"]?.jsonPrimitive?.double }.getOrNull(),
                    airDate = o["air_date"]?.jsonPrimitive?.content,
                )
            }
        }.getOrElse { emptyList() }
    }

    /** Returns true if no API key is configured. */
    suspend fun isKeyMissing(): Boolean = keyProvider().isNullOrBlank()

    private fun String.encodeUrl() = java.net.URLEncoder.encode(this, "UTF-8")
}
