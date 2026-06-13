package com.axiel7.anihyou.feature.stream.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── Episode list response ────────────────────────────────────────────────────

@Serializable
data class EpisodeListResponse(
    val mappings: Mappings? = null,
    val providers: Map<String, ProviderData> = emptyMap(),
)

@Serializable
data class Mappings(
    @SerialName("aniId") val anilistId: Int? = null,
    @SerialName("malId") val malId: Int? = null,
    @SerialName("themoviedbId") val tmdbId: Int? = null,
    @SerialName("imdbId") val imdbId: String? = null,
)

@Serializable
data class ProviderData(
    val meta: ProviderMeta? = null,
    val episodes: EpisodesByAudio = EpisodesByAudio(),
)

@Serializable
data class ProviderMeta(
    val title: String? = null,
    val image: String? = null,
    val status: String? = null,
    val totalEpisodes: Int? = null,
    val currentEpisode: Int? = null,
)

@Serializable
data class EpisodesByAudio(
    val sub: List<Episode> = emptyList(),
    val dub: List<Episode> = emptyList(),
)

@Serializable
data class Episode(
    /** Slug-form ID: "watch/kiwi/178005/sub/animepahe-1" */
    val id: String,
    val number: Int,
    val title: String? = null,
    val airDate: String? = null,
    /** Duration in seconds */
    val duration: Int? = null,
    val audio: String? = null,
    val description: String? = null,
    val filler: Boolean = false,
    val uncensored: Boolean = false,
    val image: String? = null,
) {
    val durationMinutes: Int? get() = duration?.let { it / 60 }
}

// ─── Stream sources response ──────────────────────────────────────────────────

@Serializable
data class StreamSourcesResponse(
    val streams: List<StreamSource> = emptyList(),
    val subtitles: List<Subtitle> = emptyList(),
    val intro: SkipInterval? = null,
    val outro: SkipInterval? = null,
    val download: String? = null,
)

@Serializable
data class StreamSource(
    val url: String,
    /** "hls" or "embed" */
    val type: String,
    val quality: String? = null,
    val resolution: Resolution? = null,
    val codec: String? = null,
    val audio: String? = null,
    val fansub: String? = null,
    val isActive: Boolean = false,
    val referer: String? = null,
)

@Serializable
data class Resolution(
    val width: Int,
    val height: Int,
)

@Serializable
data class Subtitle(
    val file: String,
    val label: String? = null,
    val kind: String? = null,
)

@Serializable
data class SkipInterval(
    /** Start time in seconds */
    val start: Int,
    /** End time in seconds */
    val end: Int,
)

// ─── Spotlight / collection response ─────────────────────────────────────────

@Serializable
data class SpotlightResponse(
    val results: List<StreamAnime> = emptyList(),
)

@Serializable
data class PagedAnimeResponse(
    val page: Int = 1,
    val perPage: Int = 20,
    val total: Int = 0,
    val hasNextPage: Boolean = false,
    val results: List<StreamAnime> = emptyList(),
)

@Serializable
data class StreamAnime(
    val id: Int,
    val title: AnimeTitle? = null,
    val coverImage: CoverImage? = null,
    val bannerImage: String? = null,
    val format: String? = null,
    val season: String? = null,
    val seasonYear: Int? = null,
    val episodes: Int? = null,
    val duration: Int? = null,
    val status: String? = null,
    val averageScore: Int? = null,
    val meanScore: Int? = null,
    val popularity: Int? = null,
    val favourites: Int? = null,
    val genres: List<String> = emptyList(),
    val source: String? = null,
    val countryOfOrigin: String? = null,
    val isAdult: Boolean = false,
    val studios: Studios? = null,
    val nextAiringEpisode: AiringEpisode? = null,
    val startDate: FuzzyDate? = null,
    val endDate: FuzzyDate? = null,
    // schedule extras
    @SerialName("next_episode") val nextEpisodeNumber: Int? = null,
    val airingAt: Long? = null,
    val timeUntilAiring: Long? = null,
) {
    val displayTitle: String
        get() = title?.english ?: title?.romaji ?: "Unknown"
    val coverUrl: String?
        get() = coverImage?.extraLarge ?: coverImage?.large
}

@Serializable
data class AnimeTitle(
    val romaji: String? = null,
    val english: String? = null,
    val native: String? = null,
)

@Serializable
data class CoverImage(
    val large: String? = null,
    val extraLarge: String? = null,
    val color: String? = null,
)

@Serializable
data class Studios(
    val nodes: List<Studio> = emptyList(),
)

@Serializable
data class Studio(
    val name: String,
    val isAnimationStudio: Boolean = false,
)

@Serializable
data class AiringEpisode(
    val episode: Int? = null,
    val airingAt: Long? = null,
    val timeUntilAiring: Long? = null,
)

@Serializable
data class FuzzyDate(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null,
)

// ─── Anime info response (full detail) ───────────────────────────────────────

@Serializable
data class AnimeInfoResponse(
    val id: Int,
    val idMal: Int? = null,
    val title: AnimeTitle? = null,
    val description: String? = null,
    val coverImage: CoverImage? = null,
    val bannerImage: String? = null,
    val format: String? = null,
    val season: String? = null,
    val seasonYear: Int? = null,
    val episodes: Int? = null,
    val duration: Int? = null,
    val status: String? = null,
    val averageScore: Int? = null,
    val meanScore: Int? = null,
    val popularity: Int? = null,
    val favourites: Int? = null,
    val genres: List<String> = emptyList(),
    val tags: List<AnimeTag> = emptyList(),
    val source: String? = null,
    val countryOfOrigin: String? = null,
    val isAdult: Boolean = false,
    val studios: Studios? = null,
    val nextAiringEpisode: AiringEpisode? = null,
    val startDate: FuzzyDate? = null,
    val endDate: FuzzyDate? = null,
    val trailer: Trailer? = null,
    val externalLinks: List<ExternalLink> = emptyList(),
    val synonyms: List<String> = emptyList(),
    val siteUrl: String? = null,
) {
    val displayTitle: String
        get() = title?.english ?: title?.romaji ?: "Unknown"
    val coverUrl: String?
        get() = coverImage?.extraLarge ?: coverImage?.large
    val mainStudio: String?
        get() = studios?.nodes?.firstOrNull { it.isAnimationStudio }?.name
}

@Serializable
data class AnimeTag(
    val name: String,
    val rank: Int? = null,
    val isMediaSpoiler: Boolean = false,
)

@Serializable
data class Trailer(
    val id: String? = null,
    val site: String? = null,
    val thumbnail: String? = null,
)

@Serializable
data class ExternalLink(
    val url: String,
    val site: String,
    val type: String? = null,
)

// ─── Local preferences (stored in DataStore) ─────────────────────────────────

enum class AudioType(val value: String) {
    SUB("sub"),
    DUB("dub");
    companion object {
        fun from(value: String) = entries.firstOrNull { it.value == value } ?: SUB
    }
}

data class EpisodeNote(
    val animeId: Int,
    val episodeNumber: Int,
    val note: String,
)
