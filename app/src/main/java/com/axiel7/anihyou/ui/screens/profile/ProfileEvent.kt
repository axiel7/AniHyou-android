package com.axiel7.anihyou.ui.screens.profile

import com.axiel7.anihyou.ui.common.event.PagedEvent
import com.axiel7.anihyou.ui.common.event.UiEvent

interface ProfileEvent : UiEvent, PagedEvent {
    fun toggleFollow()
    fun toggleLikeActivity(id: Int)
    fun deleteActivity(id: Int)
}