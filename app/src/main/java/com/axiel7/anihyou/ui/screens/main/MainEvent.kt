package com.axiel7.anihyou.ui.screens.main

import androidx.compose.runtime.Immutable

@Immutable
interface MainEvent {
    fun saveLastTab(index: Int)
}