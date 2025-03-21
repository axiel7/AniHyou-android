package com.axiel7.anihyou.feature.mediadetails.activity

import com.axiel7.anihyou.core.ui.common.event.PagedEvent

interface MediaActivityEvent : PagedEvent {
    fun setIsMine(value: Boolean)
    fun toggleLikeActivity(id: Int)
    fun deleteActivity(id: Int)
}