package com.axiel7.anihyou.ui.screens.thread

import com.axiel7.anihyou.ui.common.event.PagedEvent

interface ThreadDetailsEvent : PagedEvent {
    fun toggleLikeThread()
    suspend fun toggleLikeComment(id: Int): Boolean
}