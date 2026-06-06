package com.axiel7.anihyou.core.network.api

import com.axiel7.anihyou.core.base.TVDB_API_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * TheTVDB v4 API — used exclusively to retrieve English dub air dates.
 * API key is supplied at runtime from DataStore (set in Settings → TheTVDB API Key).
 */
class TvdbApi(
    private val okHttpClient: OkHttpClient,
    private val apiKeyProvider: () -> String?,
) {

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    private var cachedToken: String? = null

    // ── Auth ─────────────────────────────────────────────────────────────────

    private suspend fun getToken(): String? {
        cachedToken?.let { return it }
        val apiKey = apiKeyProvider() ?: return null
        return withContext(Dispatchers.IO) {
            runCatching {
                val body = """{"apikey":"$apiKey"}"""
                    .toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("$TVDB_API_URL/login")
                    .post(body)
                    .build()
                val response = okHttpClient.newCall(request).execute()
                val responseBody = response.body?.string() ?: return@runCatching null
                val loginResponse = json.decodeFromString<TvdbLoginResponse>(responseBody)
                loginResponse.data?.token?.also { cachedToken = it }
            }.getOrNull()
        }
    }

    // ── Search by name ────────────────────────────────────────────────────────

    /**
     * Search TheTVDB by anime title. Returns the first matching series ID.
     * Call this once per media; cache the result alongside your AniList media.
     */
    suspend fun searchSeries(title: String): TvdbSeriesResult? {
        val token = getToken() ?: return null
        return withContext(Dispatchers.IO) {
            runCatching {
                val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
                val request = Request.Builder()
                    .url("$TVDB_API_URL/search?query=$encodedTitle&type=series&limit=1")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()
                val response = okHttpClient.newCall(request).execute()
                val body = response.body?.string() ?: return@runCatching null
                val searchResponse = json.decodeFromString<TvdbSearchResponse>(body)
                searchResponse.data?.firstOrNull()
            }.getOrNull()
        }
    }

    // ── Episodes ──────────────────────────────────────────────────────────────

    /**
     * Fetch all episodes for a TVDB series ID, filtered to English language.
     * Returns a flat list of [TvdbEpisode] with aired dates.
     *
     * The `aired` field is the date the episode first aired on any network.
     * For dubbed episodes on Crunchyroll/Funimation this is the English dub date.
     *
     * Note: TVDB paginates at 500 episodes; for most anime one page is sufficient.
     */
    suspend fun getSeriesEpisodes(
        tvdbId: Int,
        season: Int? = null,
        page: Int = 0,
    ): List<TvdbEpisode> {
        val token = getToken() ?: return emptyList()
        return withContext(Dispatchers.IO) {
            runCatching {
                val seasonParam = if (season != null) "&season=$season" else ""
                val request = Request.Builder()
                    .url("$TVDB_API_URL/series/$tvdbId/episodes/default/eng?page=$page$seasonParam")
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()
                val response = okHttpClient.newCall(request).execute()
                val body = response.body?.string() ?: return@runCatching emptyList()
                json.decodeFromString<TvdbEpisodesResponse>(body).data?.episodes ?: emptyList()
            }.getOrElse { emptyList() }
        }
    }

    /**
     * Convenience: get the English dub air date for a specific episode.
     * Returns null if not found or not yet aired.
     */
    suspend fun getDubAirDate(tvdbId: Int, season: Int, episode: Int): String? {
        return getSeriesEpisodes(tvdbId, season).firstOrNull {
            it.seasonNumber == season && it.number == episode
        }?.aired
    }
}

// ── Response models ───────────────────────────────────────────────────────────

@Serializable
data class TvdbLoginResponse(
    val data: TvdbTokenData? = null,
    val status: String? = null,
)

@Serializable
data class TvdbTokenData(val token: String? = null)

@Serializable
data class TvdbSearchResponse(
    val data: List<TvdbSeriesResult>? = null,
    val status: String? = null,
)

@Serializable
data class TvdbSeriesResult(
    val tvdb_id: String? = null,
    val name: String? = null,
    val image_url: String? = null,
    val year: String? = null,
    val overview: String? = null,
)

@Serializable
data class TvdbEpisodesResponse(
    val data: TvdbEpisodesData? = null,
    val status: String? = null,
)

@Serializable
data class TvdbEpisodesData(
    val episodes: List<TvdbEpisode>? = null,
    val series: TvdbSeriesSummary? = null,
)

@Serializable
data class TvdbEpisode(
    val id: Int = 0,
    val seriesId: Int = 0,
    val name: String? = null,
    val aired: String? = null,           // "YYYY-MM-DD" English dub date
    val runtime: Int? = null,
    val seasonNumber: Int = 0,
    val number: Int = 0,                 // episode number within the season
    val absoluteNumber: Int? = null,
    val overview: String? = null,
    val image: String? = null,
)

@Serializable
data class TvdbSeriesSummary(
    val id: Int = 0,
    val name: String? = null,
)
