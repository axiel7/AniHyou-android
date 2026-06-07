package com.axiel7.anihyou.feature.mediadetails.episodes

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

private const val TMDB_BASE = "https://api.themoviedb.org/3"

class EpisodesViewModel(
    private val prefs: DefaultPreferencesRepository,
    private val okHttpClient: OkHttpClient,
) : UiStateViewModel<EpisodesUiState>() {

    override val initialState = EpisodesUiState()

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    /** Call once when the tab opens. Finds the TMDB show, loads seasons. */
    fun load(
        englishTitle: String?,
        romajiTitle: String?,
        nativeTitle: String?,
    ) {
        viewModelScope.launch {
            val source = prefs.episodeSource.first()
            mutableUiState.update { it.copy(isLoading = true, source = source, notFound = false, noApiKey = false) }

            when (source) {
                "anilist" -> {
                    // AniList doesn't expose season/episode metadata via its API.
                    // Show a helpful message — user should switch to TMDB.
                    mutableUiState.update {
                        it.copy(isLoading = false, notFound = true)
                    }
                }
                "tvdb" -> {
                    // TVDB source — notify that it's optional; handled via DubSchedule path.
                    // For now show not-found so user knows to pick TMDB.
                    mutableUiState.update {
                        it.copy(isLoading = false, notFound = true)
                    }
                }
                else -> loadFromTmdb(englishTitle, romajiTitle, nativeTitle)
            }
        }
    }

    fun selectSeason(seasonNumber: Int) {
        viewModelScope.launch {
            mutableUiState.update { it.copy(selectedSeason = seasonNumber, isLoading = true) }
            val id = mutableUiState.value.tmdbId ?: return@launch
            loadSeasonEpisodes(id, seasonNumber)
        }
    }

    // ── TMDB ─────────────────────────────────────────────────────────────────

    private suspend fun loadFromTmdb(
        englishTitle: String?,
        romajiTitle: String?,
        nativeTitle: String?,
    ) {
        val apiKey = prefs.tmdbApiKey.first()
        if (apiKey.isNullOrBlank()) {
            mutableUiState.update { it.copy(isLoading = false, noApiKey = true) }
            return
        }

        val query = (englishTitle ?: romajiTitle ?: nativeTitle ?: "").trim()
        if (query.isBlank()) {
            mutableUiState.update { it.copy(isLoading = false, notFound = true) }
            return
        }

        val searchResult = withContext(Dispatchers.IO) {
            runCatching {
                val url = "$TMDB_BASE/search/tv?api_key=$apiKey&query=${query.encodeUrl()}&page=1"
                val resp = okHttpClient.newCall(Request.Builder().url(url).build()).execute()
                val body = resp.body?.string() ?: return@runCatching null
                val root = json.parseToJsonElement(body).jsonObject
                val results = root["results"]?.jsonArray ?: return@runCatching null
                if (results.isEmpty()) return@runCatching null
                val first = results[0].jsonObject
                val id = first["id"]?.jsonPrimitive?.int?.toString() ?: return@runCatching null
                id
            }.getOrNull()
        }

        if (searchResult == null) {
            mutableUiState.update { it.copy(isLoading = false, notFound = true) }
            return
        }

        // Fetch show details to get seasons list
        val seasons = withContext(Dispatchers.IO) {
            runCatching {
                val url = "$TMDB_BASE/tv/$searchResult?api_key=$apiKey"
                val resp = okHttpClient.newCall(Request.Builder().url(url).build()).execute()
                val body = resp.body?.string() ?: return@runCatching emptyList<TmdbSeason>()
                val root = json.parseToJsonElement(body).jsonObject
                val rawSeasons = root["seasons"]?.jsonArray ?: return@runCatching emptyList<TmdbSeason>()
                rawSeasons.mapNotNull { s ->
                    val o = s.jsonObject
                    val num = o["season_number"]?.jsonPrimitive?.int ?: return@mapNotNull null
                    if (num == 0) return@mapNotNull null // skip specials
                    TmdbSeason(
                        number = num,
                        name = o["name"]?.jsonPrimitive?.content ?: "Season $num",
                        episodeCount = o["episode_count"]?.jsonPrimitive?.int ?: 0,
                        posterPath = o["poster_path"]?.jsonPrimitive?.content,
                    )
                }
            }.getOrElse { emptyList() }
        }

        val firstSeason = seasons.firstOrNull()?.number ?: 1
        mutableUiState.update {
            it.copy(tmdbId = searchResult, seasons = seasons, selectedSeason = firstSeason, isLoading = seasons.isEmpty())
        }
        if (seasons.isNotEmpty()) {
            loadSeasonEpisodes(searchResult, firstSeason)
        }
    }

    private suspend fun loadSeasonEpisodes(tmdbId: String, season: Int) {
        val apiKey = prefs.tmdbApiKey.first() ?: return
        val episodes = withContext(Dispatchers.IO) {
            runCatching {
                val url = "$TMDB_BASE/tv/$tmdbId/season/$season?api_key=$apiKey"
                val resp = okHttpClient.newCall(Request.Builder().url(url).build()).execute()
                val body = resp.body?.string() ?: return@runCatching emptyList<TmdbEpisode>()
                val root = json.parseToJsonElement(body).jsonObject
                val eps = root["episodes"]?.jsonArray ?: return@runCatching emptyList<TmdbEpisode>()
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
        mutableUiState.update { it.copy(isLoading = false, episodes = episodes) }
    }

    private fun String.encodeUrl() = java.net.URLEncoder.encode(this, "UTF-8")
}
