package com.axiel7.anihyou.feature.profile.favorites

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent

@Immutable
interface UserFavoritesEvent : PagedEvent {
    fun setType(value: FavoritesType)
}