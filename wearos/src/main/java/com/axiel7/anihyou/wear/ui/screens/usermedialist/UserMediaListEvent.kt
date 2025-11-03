package com.axiel7.anihyou.wear.ui.screens.usermedialist

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface UserMediaListEvent: PagedEvent, UiEvent {
    fun refreshList()
}