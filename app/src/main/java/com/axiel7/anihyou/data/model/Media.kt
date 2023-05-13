package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.utils.UNKNOWN_CHAR

fun MediaDetailsQuery.Media.duration() = when (this.type) {
    MediaType.ANIME -> episodes
    MediaType.MANGA -> chapters
    else -> null
}

@Composable
fun MediaDetailsQuery.Media.durationText() = when (this.type) {
    MediaType.ANIME -> stringResource(R.string.num_episodes, episodes ?: UNKNOWN_CHAR)
    MediaType.MANGA -> stringResource(R.string.num_chapters, chapters ?: UNKNOWN_CHAR)
    else -> UNKNOWN_CHAR
}