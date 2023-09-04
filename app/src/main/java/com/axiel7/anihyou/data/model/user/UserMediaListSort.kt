package com.axiel7.anihyou.data.model.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.type.MediaListSort

enum class UserMediaListSort(
    val value: MediaListSort
) : Localizable {
    SCORE_DESC(MediaListSort.SCORE_DESC) {
        @Composable
        override fun localized() = stringResource(R.string.sort_score)
    },
    UPDATED_DESC(MediaListSort.UPDATED_TIME_DESC) {
        @Composable
        override fun localized() = stringResource(R.string.sort_updated)
    },
    ADDED_DESC(MediaListSort.ADDED_TIME_DESC) {
        @Composable
        override fun localized() = stringResource(R.string.sort_added)
    };

    companion object {
        fun valueOf(rawValue: String?): UserMediaListSort? =
            entries.find { it.value.rawValue == rawValue }

        fun valueOf(value: MediaListSort?): UserMediaListSort? =
            entries.find { it.value == value }
    }
}
