package com.axiel7.anihyou.core.domain.repository

import com.axiel7.anihyou.core.model.activity.ActivityFragmentUnion
import com.axiel7.anihyou.core.network.api.LikeApi
import com.axiel7.anihyou.core.network.fragment.ListActivityFragment
import com.axiel7.anihyou.core.network.fragment.MessageActivityFragment
import com.axiel7.anihyou.core.network.fragment.TextActivityFragment
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.network.type.LikeableType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LikeRepository(
    private val api: LikeApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    private val _lastActivityLiked = MutableStateFlow<ActivityFragmentUnion?>(null)
    val lastActivityLiked = _lastActivityLiked.asStateFlow()

    suspend fun toggleActivityLike(id: Int, type: ActivityType) = when (type) {
        ActivityType.TEXT -> toggleTextActivityLike(id) { it?.isLiked == true }

        ActivityType.MESSAGE -> toggleMessageActivityLike(id) { it?.isLiked == true }

        else -> toggleListActivityLike(id) { it?.isLiked == true }
    }

    private suspend fun <T> toggleListActivityLike(
        id: Int,
        transform: (ListActivityFragment?) -> T
    ) = api
        .toggleLikeMutation(id, LikeableType.ACTIVITY)
        .execute()
        .asDataResult { data ->
            val details = data.ToggleLikeV2?.onListActivity?.listActivityFragment?.also {
                _lastActivityLiked.emit(ActivityFragmentUnion(listActivityFragment = it))
            }
            transform(details)
        }

    suspend fun toggleListActivityLike(id: Int) = toggleListActivityLike(id) { it }

    private suspend fun <T> toggleTextActivityLike(
        id: Int,
        transform: (TextActivityFragment?) -> T
    ) = api
        .toggleLikeMutation(id, LikeableType.ACTIVITY)
        .execute()
        .asDataResult { data ->
            val details = data.ToggleLikeV2?.onTextActivity?.textActivityFragment?.also {
                _lastActivityLiked.emit(ActivityFragmentUnion(textActivityFragment = it))
            }
            transform(details)
        }

    private suspend fun <T> toggleMessageActivityLike(
        id: Int,
        transform: (MessageActivityFragment?) -> T
    ) = api
        .toggleLikeMutation(id, LikeableType.ACTIVITY)
        .execute()
        .asDataResult { data ->
            val details = data.ToggleLikeV2?.onMessageActivity?.messageActivityFragment?.also {
                _lastActivityLiked.emit(ActivityFragmentUnion(messageActivityFragment = it))
            }
            transform(details)
        }

    suspend fun toggleActivityReplyLike(id: Int) = api
        .toggleLikeMutation(id, LikeableType.ACTIVITY_REPLY)
        .execute()
        .asDataResult {
            it.ToggleLikeV2?.onActivityReply?.activityReplyFragment
        }

    suspend fun toggleThreadLike(id: Int) = api
        .toggleLikeMutation(id, LikeableType.THREAD)
        .execute()
        .asDataResult {
            it.ToggleLikeV2?.onThread?.basicThreadDetails
        }

    suspend fun toggleThreadCommentLike(id: Int) = api
        .toggleLikeMutation(id, LikeableType.THREAD_COMMENT)
        .execute()
        .asDataResult {
            it.ToggleLikeV2?.onThreadComment?.isLiked
        }
}