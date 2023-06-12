package com.axiel7.anihyou.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaType

@Composable
fun MediaType.localized() = when (this) {
    MediaType.ANIME -> stringResource(R.string.anime)
    MediaType.MANGA -> stringResource(R.string.manga)
    MediaType.UNKNOWN__ -> stringResource(R.string.unknown)
}