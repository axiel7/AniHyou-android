package com.axiel7.anihyou.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType

@Composable
fun MediaListStatus.localized(
    mediaType: MediaType = MediaType.UNKNOWN__
) = when (this) {
    MediaListStatus.CURRENT -> {
        when (mediaType) {
            MediaType.ANIME -> stringResource(R.string.watching)
            MediaType.MANGA -> stringResource(R.string.reading)
            MediaType.UNKNOWN__ -> stringResource(R.string.current)
        }
    }
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