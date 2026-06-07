package com.axiel7.anihyou.core.streaming.model

data class VideoSource(
    val url: String,
    val quality: String,
    val isM3U8: Boolean,
)

data class Episode(
    val number: Float,
    val title: String?,
    val description: String?,
    val thumbnail: String?,
    val isDub: Boolean,
    val sourceEpisodeId: String,  // internal ID used to fetch streams
)

data class EpisodeList(
    val animeId: String,
    val episodes: List<Episode>,
)

data class PlaybackInfo(
    val sources: List<VideoSource>,
    val subtitles: List<SubtitleTrack> = emptyList(),
    val intro: SkipInterval? = null,
    val outro: SkipInterval? = null,
)

data class SubtitleTrack(
    val url: String,
    val language: String,
    val label: String,
)

data class SkipInterval(
    val startSeconds: Float,
    val endSeconds: Float,
)

enum class StreamingSource(val displayName: String) {
    ALL_ANIME("AllAnime"),
}
