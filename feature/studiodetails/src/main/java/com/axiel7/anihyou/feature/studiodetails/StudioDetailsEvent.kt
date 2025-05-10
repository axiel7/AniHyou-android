package com.axiel7.anihyou.feature.studiodetails

import com.axiel7.anihyou.core.base.event.PagedEvent

interface StudioDetailsEvent : PagedEvent {
    fun toggleFavorite()
}