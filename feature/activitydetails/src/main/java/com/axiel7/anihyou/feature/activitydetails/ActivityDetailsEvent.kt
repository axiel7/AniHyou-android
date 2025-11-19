package com.axiel7.anihyou.feature.activitydetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface ActivityDetailsEvent : UiEvent {
    fun toggleLikeActivity()
    fun toggleLikeReply(id: Int)
    fun refresh()
}