package com.axiel7.anihyou.core.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import com.axiel7.anihyou.core.base.DAILYMOTION_VIDEO_URL
import com.axiel7.anihyou.core.base.YOUTUBE_VIDEO_URL
import com.axiel7.anihyou.core.common.utils.StringUtils.slugify
import com.axiel7.anihyou.core.network.MediaDetailsQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.type.ExternalLinkType
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R

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
    MediaType.ANIME -> episodes?.let { episodes ->
        pluralStringResource(
            id = R.plurals.num_episodes,
            count = episodes,
            episodes
        )
    }

    MediaType.MANGA -> chapters?.let { chapters ->
        pluralStringResource(
            id = R.plurals.num_chapters,
            count = chapters,
            chapters
        )
    }

    else -> null
}

fun MediaDetailsQuery.Media.siteUrlWithTitle() =
    siteUrl.orEmpty() + "/" + basicMediaDetails.title?.userPreferred?.slugify().orEmpty()

@Composable
fun MediaDetailsQuery.Media.seasonAndYear(): String? {
    return if (season != null && seasonYear != null) "${season!!.localized()} $seasonYear"
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
    externalLinks?.filterNotNull()?.filter { it.type == ExternalLinkType.STREAMING }

fun MediaDetailsQuery.Media.externalLinks() =
    externalLinks?.filterNotNull()?.filter { it.type != ExternalLinkType.STREAMING }

fun MediaDetailsQuery.ExternalLink.languageShort() = when (language) {
    "Japanese" -> "JP"
    "English" -> "EN"
    else -> language
}