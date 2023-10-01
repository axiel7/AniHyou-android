package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.App
import com.axiel7.anihyou.NotificationsQuery
import com.axiel7.anihyou.data.PreferencesDataStore.LAST_NOTIFICATION_CREATED_AT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
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
            if (notificationsPage != null) {
                val lastCreatedAt = App.dataStore
                    .getValueSync(LAST_NOTIFICATION_CREATED_AT_PREFERENCE_KEY)
                emit(
                    PagedResult.Success(
                        data = notificationsPage.notifications?.filterNotNull().orEmpty()
                            .toGenericNotifications(
                                lastCreatedAt = lastCreatedAt ?: 0
                            ),
                        nextPage = if (notificationsPage.pageInfo?.hasNextPage == true)
                            notificationsPage.pageInfo.currentPage?.plus(1)
                        else null
                    )
                )
            }
            else emit(PagedResult.Error(message = "Empty"))
        }
    }

    private fun List<NotificationsQuery.Notification>.toGenericNotifications(
        lastCreatedAt: Int = 0
    ): List<GenericNotification> {
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
                        createdAt = noti.createdAt,
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        createdAt = noti.createdAt,
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        createdAt = noti.createdAt,
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        createdAt = noti.createdAt,
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        createdAt = noti.createdAt,
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        createdAt = noti.createdAt,
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        createdAt = noti.createdAt,
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        createdAt = noti.createdAt,
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
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
                        isUnread = lastCreatedAt < (noti.createdAt ?: 0),
                    )
                )
            }
        }
        return tempList
    }
}