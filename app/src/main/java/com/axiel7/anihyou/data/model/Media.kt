package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.utils.UNKNOWN_CHAR

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