package com.axiel7.anihyou.core.model.activity

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.axiel7.anihyou.core.network.fragment.ListActivityFragment
import com.axiel7.anihyou.core.resources.R

private const val watchedStatus = "watched episode"
private const val rewatchedEpisodeStatus = "rewatched episode"
private const val rewatchedStatus = "rewatched"
private const val plansToWatchStatus = "plans to watch"
private const val readStatus = "read chapter"
private const val rereadChapterStatus = "reread chapter"
private const val rereadStatus = "reread"
private const val plansToReadStatus = "plans to read"
private const val completedStatus = "completed"
private const val droppedStatus = "dropped"
private const val pausedWatchingStatus = "paused watching"
private const val pausedReadingStatus = "paused reading"

@Composable
fun ListActivityFragment.text(): String {
    val mediaTitle = media?.title?.userPreferred.orEmpty()
    return if (progress != null && progress != "0") {
        when (status) {
            watchedStatus -> stringResource(R.string.watched_episode_of, progress!!, mediaTitle)
            rewatchedEpisodeStatus -> stringResource(R.string.rewatched_episode_of, progress!!, mediaTitle)
            rewatchedStatus -> stringResource(R.string.rewatched_media, mediaTitle)
            readStatus -> stringResource(R.string.read_chapter_of, progress!!, mediaTitle)
            rereadChapterStatus -> stringResource(R.string.reread_chapter_of, progress!!, mediaTitle)
            rereadStatus -> stringResource(R.string.reread_media, mediaTitle)
            else -> "${status?.capitalize(Locale.current)} $progress of $mediaTitle"
        }
    } else {
        when (status) {
            plansToWatchStatus -> stringResource(R.string.plans_to_watch_anime, mediaTitle)
            plansToReadStatus -> stringResource(R.string.plans_to_read_manga, mediaTitle)
            completedStatus -> stringResource(R.string.completed_media, mediaTitle)
            droppedStatus -> stringResource(R.string.dropped_media, mediaTitle)
            pausedWatchingStatus, pausedReadingStatus -> stringResource(R.string.paused_media, mediaTitle)
            else -> "${status?.capitalize(Locale.current)} $mediaTitle"
        }
    }
}

fun ListActivityFragment.updateLikeStatus(isLiked: Boolean) = copy(
    isLiked = isLiked,
    likeCount = if (isLiked) likeCount + 1 else likeCount - 1
)