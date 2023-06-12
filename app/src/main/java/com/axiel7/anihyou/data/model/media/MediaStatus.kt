package com.axiel7.anihyou.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaStatus

@Composable
fun MediaStatus?.localized() = when (this) {
    MediaStatus.FINISHED -> stringResource(R.string.finished)
    MediaStatus.RELEASING -> stringResource(R.string.releasing)
    MediaStatus.NOT_YET_RELEASED -> stringResource(R.string.not_yet_released)
    MediaStatus.CANCELLED -> stringResource(R.string.cancelled)
    MediaStatus.HIATUS -> stringResource(R.string.hiatus)
    else -> stringResource(R.string.unknown)
}