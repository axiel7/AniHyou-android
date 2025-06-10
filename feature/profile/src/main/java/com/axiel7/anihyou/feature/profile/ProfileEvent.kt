package com.axiel7.anihyou.feature.profile

import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

interface ProfileEvent : UiEvent, PagedEvent {
    fun toggleFollow()
    fun toggleLikeActivity(id: Int)
    fun deleteActivity(id: Int)
    fun onRefresh()
    fun onRefreshActivities()
}