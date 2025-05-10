package com.axiel7.anihyou.feature.profile.favorites

import com.axiel7.anihyou.core.base.event.PagedEvent

interface UserFavoritesEvent : PagedEvent {
    fun setType(value: FavoritesType)
}