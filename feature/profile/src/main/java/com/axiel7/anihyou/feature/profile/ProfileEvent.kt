package com.axiel7.anihyou.feature.profile

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.type.ActivityType

@Immutable
interface ProfileEvent : UiEvent, PagedEvent {
    fun toggleFollow()
    fun toggleLikeActivity(id: Int, type: ActivityType)
    fun deleteActivity(id: Int)
    fun onRefresh()
    fun onRefreshActivities()
}