package com.axiel7.anihyou.data.repository

import com.axiel7.anihyou.data.api.LikeApi
import com.axiel7.anihyou.data.model.asDataResult
import com.axiel7.anihyou.fragment.ListActivityFragment
import com.axiel7.anihyou.fragment.MessageActivityFragment
import com.axiel7.anihyou.fragment.TextActivityFragment
import com.axiel7.anihyou.type.ActivityType
import com.axiel7.anihyou.type.LikeableType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeRepository @Inject constructor(
    private val api: LikeApi
) {

    fun toggleActivityLike(id: Int, type: ActivityType) = when (type) {
        ActivityType.TEXT -> toggleTextActivityLike(id) { it?.isLiked == true }

        ActivityType.MESSAGE -> toggleMessageActivityLike(id) { it?.isLiked == true }

        else -> toggleListActivityLike(id) { it?.isLiked == true }
    }

    private fun <T> toggleListActivityLike(
        id: Int,
        transform: (ListActivityFragment?) -> T
    ) = api
        .toggleLikeMutation(id, LikeableType.ACTIVITY)
        .toFlow()
        .asDataResult { data ->
            val details = data.ToggleLikeV2?.onListActivity?.listActivityFragment
            transform(details)
        }

    fun toggleListActivityLike(id: Int) = toggleListActivityLike(id) { it }

    private fun <T> toggleTextActivityLike(
        id: Int,
        transform: (TextActivityFragment?) -> T
    ) = api
        .toggleLikeMutation(id, LikeableType.ACTIVITY)
        .toFlow()
        .asDataResult { data ->
            val details = data.ToggleLikeV2?.onTextActivity?.textActivityFragment
            transform(details)
        }

    fun toggleTextActivityLike(id: Int) = toggleTextActivityLike(id) { it }

    private fun <T> toggleMessageActivityLike(
        id: Int,
        transform: (MessageActivityFragment?) -> T
    ) = api
        .toggleLikeMutation(id, LikeableType.ACTIVITY)
        .toFlow()
        .asDataResult { data ->
            val details = data.ToggleLikeV2?.onMessageActivity?.messageActivityFragment
            transform(details)
        }

    fun toggleMessageActivityLike(id: Int) = toggleMessageActivityLike(id) { it }

    fun toggleActivityReplyLike(id: Int) = api
        .toggleLikeMutation(id, LikeableType.ACTIVITY_REPLY)
        .toFlow()
        .asDataResult {
            it.ToggleLikeV2?.onActivityReply?.activityReplyFragment
        }

    fun toggleThreadLike(id: Int) = api
        .toggleLikeMutation(id, LikeableType.THREAD)
        .toFlow()
        .asDataResult {
            it.ToggleLikeV2?.onThread?.basicThreadDetails
        }

    fun toggleThreadCommentLike(id: Int) = api
        .toggleLikeMutation(id, LikeableType.THREAD_COMMENT)
        .toFlow()
        .asDataResult {
            it.ToggleLikeV2?.onThreadComment?.isLiked
        }
}