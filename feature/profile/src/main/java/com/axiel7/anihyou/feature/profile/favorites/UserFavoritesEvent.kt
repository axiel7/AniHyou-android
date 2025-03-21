package com.axiel7.anihyou.feature.profile.favorites

import com.axiel7.anihyou.core.ui.common.event.PagedEvent

interface UserFavoritesEvent : PagedEvent {
    fun setType(value: FavoritesType)
}