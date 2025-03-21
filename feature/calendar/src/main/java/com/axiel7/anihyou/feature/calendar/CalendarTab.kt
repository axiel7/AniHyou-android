package com.axiel7.anihyou.feature.calendar

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.ui.common.TabRowItem
import com.axiel7.anihyou.core.resources.R

enum class CalendarTab : Localizable {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    @get:StringRes
    val stringRes
        get() = when (this) {
            MONDAY -> R.string.monday
            TUESDAY -> R.string.tuesday
            WEDNESDAY -> R.string.wednesday
            THURSDAY -> R.string.thursday
            FRIDAY -> R.string.friday
            SATURDAY -> R.string.saturday
            SUNDAY -> R.string.sunday
        }

    @Composable
    override fun localized() = stringResource(stringRes)

    companion object {
        val tabRows = entries.map { TabRowItem(value = it, title = it.stringRes) }.toTypedArray()
    }
}