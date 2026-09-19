package com.axiel7.anihyou.core.model.media

import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListSort

fun MediaListSort.isDescending() = when (this) {
    MediaListSort.MEDIA_ID,
    MediaListSort.SCORE,
    MediaListSort.STATUS,
    MediaListSort.PROGRESS,
    MediaListSort.PROGRESS_VOLUMES,
    MediaListSort.REPEAT,
    MediaListSort.PRIORITY,
    MediaListSort.STARTED_ON,
    MediaListSort.FINISHED_ON,
    MediaListSort.ADDED_TIME,
    MediaListSort.UPDATED_TIME,
    MediaListSort.MEDIA_TITLE_ROMAJI,
    MediaListSort.MEDIA_TITLE_ENGLISH,
    MediaListSort.MEDIA_TITLE_NATIVE,
    MediaListSort.MEDIA_POPULARITY -> false
        
    MediaListSort.MEDIA_ID_DESC,
    MediaListSort.SCORE_DESC,
    MediaListSort.STATUS_DESC,
    MediaListSort.PROGRESS_DESC,
    MediaListSort.PROGRESS_VOLUMES_DESC,
    MediaListSort.REPEAT_DESC,
    MediaListSort.PRIORITY_DESC,
    MediaListSort.STARTED_ON_DESC,
    MediaListSort.FINISHED_ON_DESC,
    MediaListSort.ADDED_TIME_DESC,
    MediaListSort.UPDATED_TIME_DESC,
    MediaListSort.MEDIA_TITLE_ROMAJI_DESC,
    MediaListSort.MEDIA_TITLE_ENGLISH_DESC,
    MediaListSort.MEDIA_TITLE_NATIVE_DESC,
    MediaListSort.MEDIA_POPULARITY_DESC -> true

    MediaListSort.UNKNOWN__ -> false
}

fun MediaListSort.isTitle() = when (this) {
    MediaListSort.MEDIA_TITLE_ROMAJI,
    MediaListSort.MEDIA_TITLE_ROMAJI_DESC,
    MediaListSort.MEDIA_TITLE_ENGLISH,
    MediaListSort.MEDIA_TITLE_ENGLISH_DESC,
    MediaListSort.MEDIA_TITLE_NATIVE,
    MediaListSort.MEDIA_TITLE_NATIVE_DESC -> true

    else -> false
}

fun titleComparator(desc: Boolean): Comparator<CommonMediaListEntry> =
    if (desc) {
        compareBy { it.media?.basicMediaDetails?.title?.userPreferred }
    } else {
        compareByDescending { it.media?.basicMediaDetails?.title?.userPreferred }
    }

fun MediaListSort.comparator() = when {
    isTitle() -> titleComparator(desc = isDescending())
    this == MediaListSort.SCORE -> compareBy { it.basicMediaListEntry.score }
    this == MediaListSort.SCORE_DESC -> compareByDescending { it.basicMediaListEntry.score }
    this == MediaListSort.PROGRESS -> compareBy { it.basicMediaListEntry.progress }
    this == MediaListSort.PROGRESS_DESC -> compareByDescending { it.basicMediaListEntry.progress }
    this == MediaListSort.UPDATED_TIME -> compareBy { it.basicMediaListEntry.updatedAt }
    this == MediaListSort.UPDATED_TIME_DESC -> compareByDescending { it.basicMediaListEntry.updatedAt }
    this == MediaListSort.ADDED_TIME -> compareBy { it.basicMediaListEntry.createdAt }
    this == MediaListSort.ADDED_TIME_DESC -> compareByDescending { it.basicMediaListEntry.createdAt }
    this == MediaListSort.STARTED_ON -> compareBy {
        val date = it.basicMediaListEntry.startedAt?.fuzzyDate
        (date?.year ?: 0) * 10000 + (date?.month ?: 0) * 100 + (date?.day ?: 0)
    }

    this == MediaListSort.STARTED_ON_DESC -> compareByDescending {
        val date = it.basicMediaListEntry.startedAt?.fuzzyDate
        (date?.year ?: 0) * 10000 + (date?.month ?: 0) * 100 + (date?.day ?: 0)
    }

    this == MediaListSort.FINISHED_ON -> compareBy {
        val date = it.basicMediaListEntry.completedAt?.fuzzyDate
        (date?.year ?: 0) * 10000 + (date?.month ?: 0) * 100 + (date?.day ?: 0)
    }

    this == MediaListSort.FINISHED_ON_DESC -> compareByDescending {
        val date = it.basicMediaListEntry.completedAt?.fuzzyDate
        (date?.year ?: 0) * 10000 + (date?.month ?: 0) * 100 + (date?.day ?: 0)
    }

    this == MediaListSort.REPEAT -> compareBy { it.basicMediaListEntry.repeat }
    this == MediaListSort.REPEAT_DESC -> compareByDescending { it.basicMediaListEntry.repeat }
    this == MediaListSort.PRIORITY -> compareBy { it.basicMediaListEntry.priority }
    this == MediaListSort.PRIORITY_DESC -> compareByDescending { it.basicMediaListEntry.priority }
    this == MediaListSort.MEDIA_ID -> compareBy { it.mediaId }
    this == MediaListSort.MEDIA_ID_DESC -> compareByDescending { it.mediaId }
    else -> compareBy { it.mediaId }
}