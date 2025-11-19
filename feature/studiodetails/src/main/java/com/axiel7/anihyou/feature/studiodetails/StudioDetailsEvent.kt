package com.axiel7.anihyou.feature.studiodetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface StudioDetailsEvent : UiEvent, PagedEvent {
    fun toggleFavorite()
}