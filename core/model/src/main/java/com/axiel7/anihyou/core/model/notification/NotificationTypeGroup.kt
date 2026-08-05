package com.axiel7.anihyou.core.model.notification

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.network.type.NotificationType
import com.axiel7.anihyou.core.resources.R

enum class NotificationTypeGroup(val values: Array<NotificationType>?) : Localizable {
    ALL(null),
    AIRING(
        arrayOf(
            NotificationType.AIRING
        )
    ),
    ACTIVITY(
        arrayOf(
            NotificationType.ACTIVITY_LIKE,
            NotificationType.ACTIVITY_REPLY,
            NotificationType.ACTIVITY_REPLY_LIKE,
            NotificationType.ACTIVITY_MENTION,
            NotificationType.ACTIVITY_MESSAGE,
            NotificationType.ACTIVITY_REPLY_SUBSCRIBED
        )
    ),
    FORUM(
        arrayOf(
            NotificationType.THREAD_LIKE,
            NotificationType.THREAD_COMMENT_REPLY,
            NotificationType.THREAD_COMMENT_LIKE,
            NotificationType.THREAD_COMMENT_MENTION,
            NotificationType.THREAD_SUBSCRIBED
        )
    ),
    FOLLOWS(
        arrayOf(
            NotificationType.FOLLOWING
        )
    ),
    MEDIA(
        arrayOf(
            NotificationType.RELATED_MEDIA_ADDITION,
            NotificationType.MEDIA_DATA_CHANGE,
            NotificationType.MEDIA_MERGE,
            NotificationType.MEDIA_DELETION
        )
    ),
    SUBMISSION(
        arrayOf(
            NotificationType.MEDIA_SUBMISSION_UPDATE,
            NotificationType.STAFF_SUBMISSION_UPDATE,
            NotificationType.CHARACTER_SUBMISSION_UPDATE,
        )
    );

    @Composable
    override fun localized() = when (this) {
        ALL -> stringResource(R.string.notifications_all)
        AIRING -> stringResource(R.string.notifications_airing)
        ACTIVITY -> stringResource(R.string.activity)
        FORUM -> stringResource(R.string.forum)
        FOLLOWS -> stringResource(R.string.following)
        MEDIA -> stringResource(R.string.notifications_media)
        SUBMISSION -> stringResource(R.string.notifications_submission)
    }
}