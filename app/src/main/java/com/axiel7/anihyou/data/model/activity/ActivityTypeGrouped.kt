package com.axiel7.anihyou.data.model.activity

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.type.ActivityType

enum class ActivityTypeGrouped(val value: List<ActivityType>) : Localizable {
    TEXT(
        listOf(
            ActivityType.TEXT,
            ActivityType.MESSAGE,
        )
    ),
    LIST_PROGRESS(
        listOf(
            ActivityType.MEDIA_LIST,
            ActivityType.ANIME_LIST,
            ActivityType.MANGA_LIST,
        )
    );

    @Composable
    override fun localized() = when (this) {
        TEXT -> stringResource(R.string.text_status)
        LIST_PROGRESS -> stringResource(R.string.list_progress)
    }
}