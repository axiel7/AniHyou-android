package com.axiel7.anihyou.feature.home.activity

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface ActivityFeedEvent : UiEvent, PagedEvent {
    fun setIsFollowing(value: Boolean)
    fun setType(value: ActivityTypeGrouped)
    fun setFollowingFilters(value: List<Int>)
    fun getUserFollowing()
    fun refreshList()
    fun toggleLikeActivity(id: Int)
    fun deleteActivity(id: Int)
}