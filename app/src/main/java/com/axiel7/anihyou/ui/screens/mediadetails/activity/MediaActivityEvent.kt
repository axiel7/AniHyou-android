package com.axiel7.anihyou.ui.screens.mediadetails.activity

import com.axiel7.anihyou.ui.common.event.PagedEvent

interface MediaActivityEvent : PagedEvent {
    fun setIsMine(value: Boolean)
    fun toggleLikeActivity(id: Int)
    fun deleteActivity(id: Int)
}