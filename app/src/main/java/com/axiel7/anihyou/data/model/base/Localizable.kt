package com.axiel7.anihyou.data.model.base

import androidx.compose.runtime.Composable

interface Localizable {
    @Composable
    fun localized(): String
}