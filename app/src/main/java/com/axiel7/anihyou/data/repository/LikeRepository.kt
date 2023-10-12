package com.axiel7.anihyou.data.repository

import com.axiel7.anihyou.data.api.LikeApi
import com.axiel7.anihyou.data.model.asDataResult
import com.axiel7.anihyou.type.LikeableType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeRepository @Inject constructor(
    private val api: LikeApi
) {

    fun toggleLike(
        likeableId: Int,
        type: LikeableType
    ) = api
        .toggleLikeMutation(likeableId, type)
        .toFlow()
        .asDataResult {
            it.ToggleLikeV2?.onListActivity?.isLiked
                ?: it.ToggleLikeV2?.onTextActivity?.isLiked
                ?: it.ToggleLikeV2?.onMessageActivity?.isLiked
                ?: it.ToggleLikeV2?.onActivityReply?.isLiked
                ?: it.ToggleLikeV2?.onThread?.isLiked
                ?: it.ToggleLikeV2?.onThreadComment?.isLiked
        }
}