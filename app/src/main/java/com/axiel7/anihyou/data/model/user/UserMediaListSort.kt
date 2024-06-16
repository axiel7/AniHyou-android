package com.axiel7.anihyou.data.model.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.type.MediaListSort

enum class UserMediaListSort(
    val asc: MediaListSort,
    val desc: MediaListSort,
) : Localizable {
    // Use Romaji as default and change it later based on the user title language setting
    TITLE(
        asc = MediaListSort.MEDIA_TITLE_ROMAJI,
        desc = MediaListSort.MEDIA_TITLE_ROMAJI_DESC
    ),
    SCORE(
        asc = MediaListSort.SCORE,
        desc = MediaListSort.SCORE_DESC
    ),
    PROGRESS(
        asc = MediaListSort.PROGRESS,
        desc = MediaListSort.PROGRESS_DESC
    ),
    UPDATED(
        asc = MediaListSort.UPDATED_TIME,
        desc = MediaListSort.UPDATED_TIME_DESC
    ),
    ADDED(
        asc = MediaListSort.ADDED_TIME,
        desc = MediaListSort.ADDED_TIME_DESC
    ),
    STARTED(
        asc = MediaListSort.STARTED_ON,
        desc = MediaListSort.STARTED_ON_DESC
    ),
    FINISHED(
        asc = MediaListSort.FINISHED_ON,
        desc = MediaListSort.FINISHED_ON_DESC
    ),
    REPEAT(
        asc = MediaListSort.REPEAT,
        desc = MediaListSort.REPEAT_DESC
    );

    @Composable
    override fun localized() = when (this) {
        TITLE -> stringResource(R.string.sort_title)
        SCORE -> stringResource(R.string.sort_score)
        PROGRESS -> stringResource(R.string.progress)
        UPDATED -> stringResource(R.string.sort_updated)
        ADDED -> stringResource(R.string.sort_added)
        STARTED -> stringResource(R.string.start_date)
        FINISHED -> stringResource(R.string.end_date)
        REPEAT -> stringResource(R.string.repeat_count)
    }

    companion object {
        fun valueOf(rawValue: String?): UserMediaListSort? =
            entries.find {
                it.asc.rawValue == rawValue
                        || it.desc.rawValue == rawValue
            }

        fun valueOf(value: MediaListSort?): UserMediaListSort? =
            entries.find { it.asc == value || it.desc == value }
    }
}
