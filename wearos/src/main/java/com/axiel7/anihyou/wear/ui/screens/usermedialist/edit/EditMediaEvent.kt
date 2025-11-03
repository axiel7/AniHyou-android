package com.axiel7.anihyou.wear.ui.screens.usermedialist.edit

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface EditMediaEvent: UiEvent {
    fun onClickPlusOne()
    fun onClickMinusOne()
}