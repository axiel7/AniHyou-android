package com.axiel7.anihyou.feature.studiodetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent

@Immutable
interface StudioDetailsEvent : PagedEvent {
    fun toggleFavorite()
}