package com.axiel7.anihyou.ui.screens.profile

import com.axiel7.anihyou.ui.common.event.PagedEvent

interface ProfileEvent : PagedEvent {
    fun toggleFollow()
    fun toggleLikeActivity(id: Int)
}