package com.axiel7.anihyou.feature.mediadetails.activity

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface MediaActivityEvent : UiEvent, PagedEvent {
    fun setIsMine(value: Boolean)
    fun toggleLikeActivity(id: Int)
    fun deleteActivity(id: Int)
}