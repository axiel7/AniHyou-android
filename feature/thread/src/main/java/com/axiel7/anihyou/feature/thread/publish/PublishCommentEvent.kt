package com.axiel7.anihyou.feature.thread.publish

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface PublishCommentEvent: UiEvent {
    fun setWasPublished(value: Boolean)

    fun publishThreadComment(
        threadId: Int?,
        parentCommentId: Int?,
        id: Int? = null,
        text: String
    )
}