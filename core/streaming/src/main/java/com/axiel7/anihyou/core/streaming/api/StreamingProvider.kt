package com.axiel7.anihyou.core.streaming.api

import com.axiel7.anihyou.core.streaming.model.EpisodeList
import com.axiel7.anihyou.core.streaming.model.PlaybackInfo

/**
 * Contract every streaming source must implement.
 * Each provider knows how to:
 *  1. Search for an anime by AniList ID or title → get a source-specific animeId
 *  2. Fetch episode list for that animeId
 *  3. Fetch stream URLs for a specific episode
 */
interface StreamingProvider {
    val name: String

    /**
     * Find the source-specific anime ID from an AniList ID + title.
     * Returns null if not found.
     */
    suspend fun findAnimeId(anilistId: Int, title: String): String?

    /**
     * Fetch all sub + dub episodes for the given source anime ID.
     */
    suspend fun getEpisodes(animeId: String, isDub: Boolean): EpisodeList

    /**
     * Get playback info (stream URLs, subtitles, skip intervals) for one episode.
     */
    suspend fun getPlaybackInfo(episodeId: String): PlaybackInfo
}
