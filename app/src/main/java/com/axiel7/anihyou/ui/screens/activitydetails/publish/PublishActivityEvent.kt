package com.axiel7.anihyou.ui.screens.activitydetails.publish

interface PublishActivityEvent {
    fun publishActivity(
        id: Int? = null,
        text: String
    )

    fun publishActivityReply(
        activityId: Int,
        id: Int? = null,
        text: String
    )

    fun onErrorDisplayed()
}