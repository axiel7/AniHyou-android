package com.axiel7.anihyou.core.model.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.network.type.NotificationType
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class NotificationTypeGroup(val values: Array<NotificationType>?) : Localizable {
    ALL(null) {
        @Composable
        override fun localized() = stringResource(R.string.notifications_all)
    },
    AIRING(
        arrayOf(
            NotificationType.AIRING
        )
    ) {
        @Composable
        override fun localized() = stringResource(R.string.notifications_airing)
    },
    ACTIVITY(
        arrayOf(
            NotificationType.ACTIVITY_LIKE,
            NotificationType.ACTIVITY_REPLY,
            NotificationType.ACTIVITY_REPLY_LIKE,
            NotificationType.ACTIVITY_MENTION,
            NotificationType.ACTIVITY_MESSAGE,
            NotificationType.ACTIVITY_REPLY_SUBSCRIBED
        )
    ) {
        @Composable
        override fun localized() = stringResource(R.string.activity)
    },
    FORUM(
        arrayOf(
            NotificationType.THREAD_LIKE,
            NotificationType.THREAD_COMMENT_REPLY,
            NotificationType.THREAD_COMMENT_LIKE,
            NotificationType.THREAD_COMMENT_MENTION,
            NotificationType.THREAD_SUBSCRIBED
        )
    ) {
        @Composable
        override fun localized() = stringResource(R.string.forum)
    },
    FOLLOWS(
        arrayOf(
            NotificationType.FOLLOWING
        )
    ) {
        @Composable
        override fun localized() = stringResource(R.string.following)
    },
    MEDIA(
        arrayOf(
            NotificationType.RELATED_MEDIA_ADDITION,
            NotificationType.MEDIA_DATA_CHANGE,
            NotificationType.MEDIA_MERGE,
            NotificationType.MEDIA_DELETION
        )
    ) {
        @Composable
        override fun localized() = stringResource(R.string.notifications_media)
    }
}