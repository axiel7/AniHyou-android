package com.axiel7.anihyou.core.domain.repository

import com.axiel7.anihyou.core.network.api.LikeApi
import com.axiel7.anihyou.core.network.fragment.ListActivityFragment
import com.axiel7.anihyou.core.network.fragment.MessageActivityFragment
import com.axiel7.anihyou.core.network.fragment.TextActivityFragment
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.network.type.LikeableType

class LikeRepository(
    private val api: LikeApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

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