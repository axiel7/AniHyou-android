package com.axiel7.anihyou.feature.studiodetails

import com.axiel7.anihyou.core.ui.common.event.PagedEvent

interface StudioDetailsEvent : PagedEvent {
    fun toggleFavorite()
}