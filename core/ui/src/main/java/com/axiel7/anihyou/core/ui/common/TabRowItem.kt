package com.axiel7.anihyou.core.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable

@Stable
data class TabRowItem<T>(
    val value: T,
    @param:StringRes val title: Int? = null,
    @param:DrawableRes val icon: Int? = null,
)
