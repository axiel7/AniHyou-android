package com.axiel7.anihyou.feature.profile.favorites.reorder


import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface ReorderFavoritesEvent : UiEvent, PagedEvent {
    fun onRefresh()

    fun onMove(from: Int, to: Int)

    fun saveNewOrder()
}