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

fun MediaListStatus.icon() = when (this) {
    MediaListStatus.CURRENT -> R.drawable.play_circle_24
    MediaListStatus.PLANNING -> R.drawable.schedule_24
    MediaListStatus.COMPLETED -> R.drawable.check_circle_24
    MediaListStatus.DROPPED -> R.drawable.delete_24
    MediaListStatus.PAUSED -> R.drawable.pause_circle_24
    MediaListStatus.REPEATING -> R.drawable.repeat_24
    MediaListStatus.UNKNOWN__ -> R.drawable.play_circle_24
}

@Composable
fun MediaListStatus.colorScheme() = when (this) {
    MediaListStatus.CURRENT -> StatColors.greenScheme()
    MediaListStatus.PLANNING -> StatColors.grayScheme()
    MediaListStatus.COMPLETED -> StatColors.blueScheme()
    MediaListStatus.DROPPED -> StatColors.redScheme()
    MediaListStatus.PAUSED -> StatColors.yellowScheme()
    MediaListStatus.REPEATING -> StatColors.blueScheme()
    MediaListStatus.UNKNOWN__ -> StatColors.grayScheme()
}