package com.axiel7.anihyou.feature.activitydetails.publish

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface PublishActivityEvent: UiEvent {
    fun publishActivity(
        id: Int? = null,
        text: String
    )

    fun publishActivityReply(
        activityId: Int,
        id: Int? = null,
        text: String
    )
}