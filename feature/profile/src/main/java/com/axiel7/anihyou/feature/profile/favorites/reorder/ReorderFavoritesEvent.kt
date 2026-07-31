package com.axiel7.anihyou.feature.profile.favorites.reorder


import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.base.event.UiEvent

interface ReorderFavoritesEvent : UiEvent, PagedEvent {
    fun onRefresh()

    override fun onErrorDisplayed()

    fun saveNewOrder()
}