package com.axiel7.anihyou.ui.screens.profile.favorites

import com.axiel7.anihyou.ui.common.event.PagedEvent

interface UserFavoritesEvent : PagedEvent {
    fun setType(value: FavoritesType)
}