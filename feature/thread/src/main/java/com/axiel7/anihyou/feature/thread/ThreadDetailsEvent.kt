package com.axiel7.anihyou.feature.thread

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent

@Immutable
interface ThreadDetailsEvent : PagedEvent {
    fun toggleLikeThread()
    fun subscribeToThread(subscribe: Boolean)
    suspend fun toggleLikeComment(id: Int): Boolean
    fun refresh()
}