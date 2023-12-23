package com.axiel7.anihyou.ui.screens.thread.publish

interface PublishCommentEvent {
    fun setWasPublished(value: Boolean)

    fun publishThreadComment(
        threadId: Int?,
        parentCommentId: Int?,
        id: Int? = null,
        text: String
    )

    fun onErrorDisplayed()
}