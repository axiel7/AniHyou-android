package com.axiel7.anihyou.feature.home.activity

import com.axiel7.anihyou.core.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.core.ui.common.event.PagedEvent

interface ActivityFeedEvent : PagedEvent {
    fun setIsFollowing(value: Boolean)
    fun setType(value: ActivityTypeGrouped)
    fun refreshList()
    fun toggleLikeActivity(id: Int)
}