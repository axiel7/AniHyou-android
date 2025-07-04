package com.axiel7.anihyou.core.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class ItemsPerRow(val value: Int) : Localizable {
    DEFAULT(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10);

    val stringRes
        get() = when (this) {
            DEFAULT -> R.string.default_setting
            ONE -> R.string.one
            TWO -> R.string.two
            THREE -> R.string.three
            FOUR -> R.string.four
            FIVE -> R.string.five
            SIX -> R.string.six
            SEVEN -> R.string.seven
            EIGHT -> R.string.eight
            NINE -> R.string.nine
            TEN -> R.string.ten
        }

    @Composable
    override fun localized() = stringResource(stringRes)

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }

        fun valueOf(value: Int) = entries.find { it.value == value }
    }
}