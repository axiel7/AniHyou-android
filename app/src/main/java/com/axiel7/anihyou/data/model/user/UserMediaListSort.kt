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
    SCORE(
        asc = MediaListSort.SCORE,
        desc = MediaListSort.SCORE_DESC
    ),
    UPDATED(
        asc = MediaListSort.UPDATED_TIME,
        desc = MediaListSort.UPDATED_TIME_DESC
    ),
    ADDED(
        asc = MediaListSort.ADDED_TIME,
        desc = MediaListSort.ADDED_TIME_DESC
    ),
    ;

    @Composable
    override fun localized() = when (this) {
        SCORE -> stringResource(R.string.sort_score)
        UPDATED -> stringResource(R.string.sort_updated)
        ADDED -> stringResource(R.string.sort_added)
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
