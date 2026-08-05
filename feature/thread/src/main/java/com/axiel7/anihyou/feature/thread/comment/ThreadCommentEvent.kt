package com.axiel7.anihyou.feature.thread.comment

import androidx.compose.runtime.Stable
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.fragment.CommonThreadComment

@Stable
interface ThreadCommentEvent : UiEvent {
    suspend fun toggleLikeComment(id: Int): Boolean
    fun onCommentPublished(comment: CommonThreadComment)
}