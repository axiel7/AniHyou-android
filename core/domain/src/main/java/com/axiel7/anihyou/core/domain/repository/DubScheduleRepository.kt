package com.axiel7.anihyou.core.domain.repository

import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.network.api.TvdbApi
import com.axiel7.anihyou.core.network.api.TvdbEpisode
import com.axiel7.anihyou.core.network.api.TvdbSeriesResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Bridges AniList airing data with TheTVDB English dub dates.
 *
 * Usage pattern:
 *   1. Have the AniList media title (English or Romaji).
 *   2. Call [findTvdbId] once — cache the returned [TvdbSeriesResult.tvdb_id] in your UiState.
 *   3. Call [getDubSchedule] with the TVDB ID to get all dub-dated episodes for a season.
 *   4. Join by episode number against your AniList airing schedule.
 */
class DubScheduleRepository(
    private val tvdbApi: TvdbApi,
) {

    /**
     * Find the TVDB series entry for an AniList media title.
     * Try English title first, fall back to Romaji.
     */
    suspend fun findTvdbSeries(
        englishTitle: String?,
        romajiTitle: String?,
    ): DataResult<TvdbSeriesResult> = withContext(Dispatchers.IO) {
        val title = englishTitle?.takeIf { it.isNotBlank() } ?: romajiTitle
        if (title.isNullOrBlank()) return@withContext DataResult.Error("No title available")

        val result = tvdbApi.searchSeries(title)
        if (result != null) {
            DataResult.Success(result)
        } else {
            // If English failed, try romaji as fallback
            if (!englishTitle.isNullOrBlank() && !romajiTitle.isNullOrBlank()) {
                val fallback = tvdbApi.searchSeries(romajiTitle)
                if (fallback != null) DataResult.Success(fallback)
                else DataResult.Error("Series not found on TheTVDB")
            } else {
                DataResult.Error("Series not found on TheTVDB")
            }
        }
    }

    /**
     * Fetch the English dub episode list for a given TVDB series ID and season.
     * The [TvdbEpisode.aired] field is the dub air date in "YYYY-MM-DD" format.
     */
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

    /**
     * Convenience: get the dub air date string for one specific episode.
     * Returns null if the episode has no dub date yet.
     */
    suspend fun getDubAirDate(tvdbId: Int, season: Int, episode: Int): String? {
        return tvdbApi.getDubAirDate(tvdbId, season, episode)
    }
}
