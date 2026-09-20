package com.axiel7.anihyou.feature.studiodetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.type.MediaSort

@Immutable
interface StudioDetailsEvent : UiEvent, PagedEvent {
    fun toggleFavorite()
    fun setSort(sort: MediaSort)
    fun toggleOnMyList()
}