package com.axiel7.anihyou.data.model.notification

import com.axiel7.anihyou.type.NotificationType

data class GenericNotification(
    val id: Int,
    val text: String,
    val imageUrl: String?,
    val contentId: Int,
    val secondaryContentId: Int? = null,
    val type: NotificationType?,
    val createdAt: Int?,
)
