package com.axiel7.anihyou.ui.screens.home.activity

import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.ui.common.event.PagedEvent

interface ActivityFeedEvent : PagedEvent {
    fun setIsFollowing(value: Boolean)
    fun setType(value: ActivityTypeGrouped)
    fun refreshList()
    fun toggleLikeActivity(id: Int)
}