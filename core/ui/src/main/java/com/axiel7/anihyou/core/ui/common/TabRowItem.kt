package com.axiel7.anihyou.core.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class TabRowItem<T>(
    val value: T,
    @StringRes val title: Int? = null,
    @DrawableRes val icon: Int? = null,
)
