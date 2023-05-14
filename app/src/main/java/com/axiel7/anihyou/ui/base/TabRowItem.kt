package com.axiel7.anihyou.ui.base

import androidx.annotation.DrawableRes

data class TabRowItem<T>(
    val value: T,
    val title: String? = null,
    @DrawableRes val icon: Int? = null,
)
