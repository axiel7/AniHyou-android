package com.axiel7.anihyou.data.model

import com.axiel7.anihyou.type.NotificationType

data class GenericNotification(
    val id: Int,
    val text: String,
    val imageUrl: String?,
    val contentId: Int,
    val type: NotificationType?,
    val createdAt: Int?,
)
