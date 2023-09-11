package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.NotificationsQuery
import com.axiel7.anihyou.data.model.notification.GenericNotification
import com.axiel7.anihyou.data.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import kotlinx.coroutines.flow.flow

object NotificationRepository {

    fun getNotificationsPage(
        type: NotificationTypeGroup,
        resetCount: Boolean,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = NotificationsQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            typeIn = if (type == NotificationTypeGroup.ALL) Optional.absent()
            else Optional.present(type.values.toList()),
            resetCount = Optional.present(resetCount)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val notificationsPage = response?.data?.Page
            if (notificationsPage != null) emit(
                PagedResult.Success(
                    data = notificationsPage.notifications?.filterNotNull().orEmpty()
                        .toGenericNotifications(),
                    nextPage = if (notificationsPage.pageInfo?.hasNextPage == true)
                        notificationsPage.pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Empty"))
        }
    }

    private fun List<NotificationsQuery.Notification>.toGenericNotifications(): List<GenericNotification> {
        val tempList = mutableListOf<GenericNotification>()
        this.forEach { aniListNotification ->
            aniListNotification.onAiringNotification?.let { noti ->
                val episodeString = noti.contexts?.get(0) ?: ""
                val ofString = noti.contexts?.get(1) ?: ""
                val airedString = noti.contexts?.get(2) ?: ""
                val text =
                    "$episodeString${noti.episode}$ofString${noti.media?.title?.userPreferred}$airedString"
                tempList.add(
                    GenericNotification(
                        id = noti.id,
                        text = text,
                        imageUrl = noti.media?.coverImage?.medium,
                        contentId = noti.animeId,
                        type = noti.type,
                        createdAt = noti.createdAt
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
                        createdAt = noti.createdAt
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
                        createdAt = noti.createdAt
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
                        createdAt = noti.createdAt
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
                        createdAt = noti.createdAt
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
                        createdAt = noti.createdAt
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
                        createdAt = noti.createdAt
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
                        createdAt = noti.createdAt
                    )
                )
            }
            aniListNotification.onThreadCommentMentionNotification?.let { noti ->
                tempList.add(
                    GenericNotification(
                        id = noti.id,
                        text = "${noti.user?.name}${noti.context}",
                        imageUrl = noti.user?.avatar?.medium,
                        contentId = noti.commentId,
                        secondaryContentId = noti.userId,
                        type = noti.type,
                        createdAt = noti.createdAt
                    )
                )
            }
            aniListNotification.onThreadCommentReplyNotification?.let { noti ->
                tempList.add(
                    GenericNotification(
                        id = noti.id,
                        text = "${noti.user?.name}${noti.context}",
                        imageUrl = noti.user?.avatar?.medium,
                        contentId = noti.commentId,
                        secondaryContentId = noti.userId,
                        type = noti.type,
                        createdAt = noti.createdAt
                    )
                )
            }
            aniListNotification.onThreadCommentSubscribedNotification?.let { noti ->
                tempList.add(
                    GenericNotification(
                        id = noti.id,
                        text = "${noti.user?.name}${noti.context}",
                        imageUrl = noti.user?.avatar?.medium,
                        contentId = noti.commentId,
                        secondaryContentId = noti.userId,
                        type = noti.type,
                        createdAt = noti.createdAt
                    )
                )
            }
            aniListNotification.onThreadCommentLikeNotification?.let { noti ->
                tempList.add(
                    GenericNotification(
                        id = noti.id,
                        text = "${noti.user?.name}${noti.context}",
                        imageUrl = noti.user?.avatar?.medium,
                        contentId = noti.commentId,
                        secondaryContentId = noti.userId,
                        type = noti.type,
                        createdAt = noti.createdAt
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
                        createdAt = noti.createdAt
                    )
                )
            }
            aniListNotification.onRelatedMediaAdditionNotification?.let { noti ->
                tempList.add(
                    GenericNotification(
                        id = noti.id,
                        text = "${noti.media?.title?.userPreferred}${noti.context}",
                        imageUrl = noti.media?.coverImage?.medium,
                        contentId = noti.mediaId,
                        type = noti.type,
                        createdAt = noti.createdAt
                    )
                )
            }
            aniListNotification.onMediaDataChangeNotification?.let { noti ->
                tempList.add(
                    GenericNotification(
                        id = noti.id,
                        text = "${noti.media?.title?.userPreferred}${noti.context}",
                        imageUrl = noti.media?.coverImage?.medium,
                        contentId = noti.mediaId,
                        type = noti.type,
                        createdAt = noti.createdAt
                    )
                )
            }
            aniListNotification.onMediaMergeNotification?.let { noti ->
                tempList.add(
                    GenericNotification(
                        id = noti.id,
                        text = "${noti.media?.title?.userPreferred}${noti.context}",
                        imageUrl = noti.media?.coverImage?.medium,
                        contentId = noti.mediaId,
                        type = noti.type,
                        createdAt = noti.createdAt
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
                        createdAt = noti.createdAt
                    )
                )
            }
        }
        return tempList
    }
}