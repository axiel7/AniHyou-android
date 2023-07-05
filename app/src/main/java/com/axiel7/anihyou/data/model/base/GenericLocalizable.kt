package com.axiel7.anihyou.data.model.base

import androidx.compose.runtime.Composable

data class GenericLocalizable<T>(
    val value: T
) : Localizable {
    @Composable
    override fun localized() = value.toString()
}