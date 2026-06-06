package com.axiel7.anihyou.feature.calendar

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class CalendarViewMode : Localizable {
    MONTH,
    WEEK,
    DAY;

    @get:StringRes
    val stringRes
        get() = when (this) {
            MONTH -> R.string.month
            WEEK -> R.string.week
            DAY -> R.string.day
        }

    @Composable
    override fun localized() = stringResource(stringRes)
}
