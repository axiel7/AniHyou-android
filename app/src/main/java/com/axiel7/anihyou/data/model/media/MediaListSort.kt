package com.axiel7.anihyou.data.model.media

import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListSort

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