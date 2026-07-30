package com.axiel7.anihyou.feature.profile.favorites.reorder

import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

interface ReorderFavoritesEvent : UiEvent, PagedEvent {
    fun onRefresh()

    override fun onErrorDisplayed()

    fun saveNewOrder()
}