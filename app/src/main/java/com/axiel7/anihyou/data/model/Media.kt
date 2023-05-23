package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.type.ExternalLinkType
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.utils.DAILYMOTION_VIDEO_URL
import com.axiel7.anihyou.utils.UNKNOWN_CHAR
import com.axiel7.anihyou.utils.YOUTUBE_VIDEO_URL

// TODO: consider volumes
fun BasicMediaDetails.duration() = when (type) {
    MediaType.ANIME -> episodes
    MediaType.MANGA -> chapters
    else -> null
}

fun BasicMediaDetails.isAnime() = type == MediaType.ANIME
fun BasicMediaDetails.isManga() = type == MediaType.MANGA

@Composable
fun BasicMediaDetails.durationText() = when (this.type) {
    MediaType.ANIME -> stringResource(R.string.num_episodes, episodes ?: UNKNOWN_CHAR)
    MediaType.MANGA -> stringResource(R.string.num_chapters, chapters ?: UNKNOWN_CHAR)
    else -> UNKNOWN_CHAR
}

@Composable
fun MediaDetailsQuery.Media.seasonAndYear(): String? {
    return if (season != null && seasonYear != null) "${season.localized()} $seasonYear"
    else season?.localized() ?: if (seasonYear != null) "$seasonYear"
    else null
}

fun MediaDetailsQuery.Trailer.link(): String? {
    return when (site) {
        "youtube" -> YOUTUBE_VIDEO_URL + id
        "dailymotion" -> DAILYMOTION_VIDEO_URL + id
        else -> null
    }
}

fun MediaDetailsQuery.Media.streamingLinks() =
    externalLinks?.filter { it?.type == ExternalLinkType.STREAMING }

fun MediaDetailsQuery.Media.externalLinks() =
    externalLinks?.filter { it?.type != ExternalLinkType.STREAMING }