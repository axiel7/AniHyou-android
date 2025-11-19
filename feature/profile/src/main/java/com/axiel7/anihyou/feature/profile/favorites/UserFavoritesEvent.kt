package com.axiel7.anihyou.feature.profile.favorites

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface UserFavoritesEvent : UiEvent, PagedEvent {
    fun setType(value: FavoritesType)
}