package com.axiel7.anihyou.data.model.notification

import com.axiel7.anihyou.NotificationsQuery
import com.axiel7.anihyou.type.NotificationType

data class GenericNotification(
    val id: Int,
    val text: String,
    val imageUrl: String?,
    val largeImageUrl: String? = null,
    val contentId: Int,
    val secondaryContentId: Int? = null,
    val type: NotificationType?,
    val createdAt: Int?,
    val isUnread: Boolean = false,
) {
    val isMedia
        get() = when (type) {
            NotificationType.AIRING,
            NotificationType.RELATED_MEDIA_ADDITION,
            NotificationType.MEDIA_DATA_CHANGE,
            NotificationType.MEDIA_MERGE,
            NotificationType.MEDIA_DELETION -> true

            else -> false
        }

    companion object {
        fun List<NotificationsQuery.Notification>.toGenericNotifications(): List<GenericNotification> {
            val tempList = mutableListOf<GenericNotification>()
            this.forEach { aniListNotification ->
                aniListNotification.onAiringNotification?.let { noti ->
                    val episodeString = noti.contexts?.get(0).orEmpty()
                    val ofString = noti.contexts?.get(1).orEmpty()
                    val airedString = noti.contexts?.get(2).orEmpty()
                    val text =
                        "$episodeString${noti.episode}$ofString${noti.media?.title?.userPreferred}$airedString"
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = text,
                            imageUrl = noti.media?.coverImage?.medium,
                            largeImageUrl = noti.media?.coverImage?.large,
                            contentId = noti.animeId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onFollowingNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onActivityMessageNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.activityId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onActivityMentionNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.activityId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onActivityReplyNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.activityId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onActivityReplySubscribedNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.activityId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onActivityLikeNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.activityId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onActivityReplyLikeNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.activityId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onThreadCommentMentionNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}${noti.thread?.title}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.thread?.id ?: noti.commentId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onThreadCommentReplyNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}${noti.thread?.title}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.thread?.id ?: noti.commentId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onThreadCommentSubscribedNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}${noti.thread?.title}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.thread?.id ?: noti.commentId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onThreadCommentLikeNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}${noti.thread?.title}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.thread?.id ?: noti.commentId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onThreadLikeNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.user?.name}${noti.context}",
                            imageUrl = noti.user?.avatar?.medium,
                            contentId = noti.threadId,
                            secondaryContentId = noti.userId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onRelatedMediaAdditionNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.media?.title?.userPreferred}${noti.context}",
                            imageUrl = noti.media?.coverImage?.medium,
                            largeImageUrl = noti.media?.coverImage?.large,
                            contentId = noti.mediaId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onMediaDataChangeNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.media?.title?.userPreferred}${noti.context}\n${noti.reason}",
                            imageUrl = noti.media?.coverImage?.medium,
                            largeImageUrl = noti.media?.coverImage?.large,
                            contentId = noti.mediaId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onMediaMergeNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.media?.title?.userPreferred}${noti.context}",
                            imageUrl = noti.media?.coverImage?.medium,
                            largeImageUrl = noti.media?.coverImage?.large,
                            contentId = noti.mediaId,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
                aniListNotification.onMediaDeletionNotification?.let { noti ->
                    tempList.add(
                        GenericNotification(
                            id = noti.id,
                            text = "${noti.deletedMediaTitle}${noti.context}",
                            imageUrl = null,
                            contentId = 0,
                            type = noti.type,
                            createdAt = noti.createdAt,
                        )
                    )
                }
            }
            return tempList
        }
    }
}
