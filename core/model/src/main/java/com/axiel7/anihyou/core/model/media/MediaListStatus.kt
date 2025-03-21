package com.axiel7.anihyou.core.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R

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

fun String.asMediaListStatus() = when (this) {
    "Current", "Watching", "Reading" -> MediaListStatus.CURRENT
    "Planning" -> MediaListStatus.PLANNING
    "Completed" -> MediaListStatus.COMPLETED
    "Dropped" -> MediaListStatus.DROPPED
    "Paused" -> MediaListStatus.PAUSED
    "Repeating", "Rewatching", "Rereading" -> MediaListStatus.REPEATING
    else -> {
        if (startsWith("Completed")) {
            MediaListStatus.COMPLETED
        } else {
            null
        }
    }
}

@Composable
fun String.localizedListStatus() = when (this) {
    "Watching" -> stringResource(R.string.watching)
    "Reading" -> stringResource(R.string.reading)
    "Current" -> stringResource(R.string.current)
    "Planning" -> stringResource(R.string.planning)
    "Completed" -> stringResource(R.string.completed)
    "Dropped" -> stringResource(R.string.dropped)
    "Paused" -> stringResource(R.string.paused)
    "Repeating", "Rewatching", "Rereading" -> stringResource(R.string.repeating)
    else -> {
        if (startsWith("Completed")) {
            stringResource(R.string.completed) + replaceFirst("Completed", "")
        } else {
            this
        }
    }
}

/**
 * Returns if this is a status considered as active.
 * That means the user is consuming or will be consuming the series.
 */
fun MediaListStatus.isActive() =
    this == MediaListStatus.CURRENT
            || this == MediaListStatus.PLANNING
            || this == MediaListStatus.REPEATING

fun MediaListStatus.icon() = when (this) {
    MediaListStatus.CURRENT -> R.drawable.play_circle_24
    MediaListStatus.PLANNING -> R.drawable.schedule_24
    MediaListStatus.COMPLETED -> R.drawable.check_circle_24
    MediaListStatus.DROPPED -> R.drawable.delete_24
    MediaListStatus.PAUSED -> R.drawable.pause_circle_24
    MediaListStatus.REPEATING -> R.drawable.repeat_24
    MediaListStatus.UNKNOWN__ -> R.drawable.list_alt_24
}