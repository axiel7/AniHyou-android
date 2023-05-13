package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaFormat

@Composable
fun MediaFormat.localized() = when (this) {
    MediaFormat.TV -> stringResource(R.string.tv)
    MediaFormat.TV_SHORT -> stringResource(R.string.tv_short)
    MediaFormat.MOVIE -> stringResource(R.string.movie)
    MediaFormat.SPECIAL -> stringResource(R.string.special)
    MediaFormat.OVA -> stringResource(R.string.ova)
    MediaFormat.ONA -> stringResource(R.string.ona)
    MediaFormat.MUSIC -> stringResource(R.string.music)
    MediaFormat.MANGA -> stringResource(R.string.manga)
    MediaFormat.NOVEL -> stringResource(R.string.novel)
    MediaFormat.ONE_SHOT -> stringResource(R.string.one_shot)
    MediaFormat.UNKNOWN__ -> stringResource(R.string.unknown)
}