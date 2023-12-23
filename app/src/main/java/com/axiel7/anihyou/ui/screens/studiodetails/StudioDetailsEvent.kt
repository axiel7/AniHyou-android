package com.axiel7.anihyou.ui.screens.studiodetails

import com.axiel7.anihyou.ui.common.event.PagedEvent

interface StudioDetailsEvent : PagedEvent {
    fun toggleFavorite()
}