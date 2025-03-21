package com.axiel7.anihyou.core.model.activity

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class ActivityTypeGrouped(val value: List<ActivityType>) : Localizable {
    ALL(
        ActivityType.knownEntries
    ),
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
        ALL -> stringResource(R.string.all)
        TEXT -> stringResource(R.string.text_status)
        LIST_PROGRESS -> stringResource(R.string.list_progress)
    }
}