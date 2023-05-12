package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaListStatus

@Composable
fun MediaListStatus.localized() = when (this) {
    MediaListStatus.CURRENT -> stringResource(R.string.current)
    MediaListStatus.PLANNING -> stringResource(R.string.planning)
    MediaListStatus.COMPLETED -> stringResource(R.string.completed)
    MediaListStatus.DROPPED -> stringResource(R.string.dropped)
    MediaListStatus.PAUSED -> stringResource(R.string.paused)
    MediaListStatus.REPEATING -> stringResource(R.string.repeating)
    MediaListStatus.UNKNOWN__ -> stringResource(R.string.unknown)
}