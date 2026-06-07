package com.axiel7.anihyou.core.domain.repository

import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.TvdbKeyProvider
import com.axiel7.anihyou.core.network.api.TvdbApi
import com.axiel7.anihyou.core.network.api.TvdbEpisode
import com.axiel7.anihyou.core.network.api.TvdbSeriesResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DubScheduleRepository(
    private val tvdbApi: TvdbApi,
    private val keyProvider: TvdbKeyProvider,
) {

    /** True when the user hasn't configured a real TVDB API key. */
    suspend fun isKeyMissing(): Boolean {
        val key = runCatching { keyProvider() }.getOrElse { return true }
        return key.isBlank() || key == "YOUR_TVDB_API_KEY_HERE"
    }

    suspend fun findTvdbSeries(
        englishTitle: String?,
        romajiTitle: String?,
    ): DataResult<TvdbSeriesResult> = withContext(Dispatchers.IO) {
        if (isKeyMissing()) return@withContext DataResult.Error("NO_API_KEY")

        val title = englishTitle?.takeIf { it.isNotBlank() } ?: romajiTitle
        if (title.isNullOrBlank()) return@withContext DataResult.Error("No title available")

        val result = tvdbApi.searchSeries(title)
        if (result != null) {
            DataResult.Success(result)
        } else {
            if (!englishTitle.isNullOrBlank() && !romajiTitle.isNullOrBlank()) {
                val fallback = tvdbApi.searchSeries(romajiTitle)
                if (fallback != null) DataResult.Success(fallback)
                else DataResult.Error("Series not found on TheTVDB")
            } else {
                DataResult.Error("Series not found on TheTVDB")
            }
        }
    }

    suspend fun getDubSchedule(
        tvdbId: Int,
        season: Int = 1,
    ): DataResult<List<TvdbEpisode>> = withContext(Dispatchers.IO) {
        val episodes = tvdbApi.getSeriesEpisodes(tvdbId, season)
        if (episodes.isEmpty()) {
            DataResult.Error("No dub schedule found for season $season")
        } else {
            DataResult.Success(episodes)
        }
    }

    suspend fun getDubAirDate(tvdbId: Int, season: Int, episode: Int): String? {
        return tvdbApi.getDubAirDate(tvdbId, season, episode)
    }
}
