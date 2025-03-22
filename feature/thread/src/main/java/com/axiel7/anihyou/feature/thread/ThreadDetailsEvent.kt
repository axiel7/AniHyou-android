package com.axiel7.anihyou.feature.thread

import com.axiel7.anihyou.core.base.event.PagedEvent

interface ThreadDetailsEvent : PagedEvent {
    fun toggleLikeThread()
    fun subscribeToThread(subscribe: Boolean)
    suspend fun toggleLikeComment(id: Int): Boolean
    fun refresh()
}