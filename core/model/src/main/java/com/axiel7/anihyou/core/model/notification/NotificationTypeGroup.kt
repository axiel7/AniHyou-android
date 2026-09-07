package com.axiel7.anihyou.core.model.notification

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.DeepLink
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

    @get:StringRes
    val stringRes
        get() = when (this) {
            ALL -> R.string.notifications_all
            AIRING -> R.string.notifications_airing
            ACTIVITY -> R.string.activity
            FORUM -> R.string.forum
            FOLLOWS -> R.string.following
            MEDIA -> R.string.notifications_media
            SUBMISSION -> R.string.notifications_submission
        }

    @Composable
    override fun localized() = stringResource(stringRes)

    companion object {
        fun NotificationType.asGroup() = entries.find { it.values?.contains(this) == true }

        fun NotificationTypeGroup.asDeepLinkType() = when (this) {
            ALL -> null
            AIRING -> DeepLink.Type.ANIME
            ACTIVITY -> DeepLink.Type.ACTIVITY
            FORUM -> DeepLink.Type.THREAD
            FOLLOWS -> DeepLink.Type.USER
            MEDIA -> DeepLink.Type.ANIME
            SUBMISSION -> null
        }
    }
}