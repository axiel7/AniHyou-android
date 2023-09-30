package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ToggleLikeMutation
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryMutation
import com.axiel7.anihyou.type.LikeableType
import kotlinx.coroutines.flow.flow

object LikeRepository {

    fun toggleLike(
        likeableId: Int,
        type: LikeableType
    ) = flow {
        emit(DataResult.Loading)

        val response = ToggleLikeMutation(
            likeableId = Optional.present(likeableId),
            type = Optional.present(type)
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val data = response?.data
            if (data?.ToggleLikeV2 != null) {
                val liked = data.ToggleLikeV2.onListActivity?.isLiked
                        ?: data.ToggleLikeV2.onTextActivity?.isLiked
                        ?: data.ToggleLikeV2.onMessageActivity?.isLiked
                        ?: data.ToggleLikeV2.onActivityReply?.isLiked
                        ?: data.ToggleLikeV2.onThread?.isLiked
                        ?: data.ToggleLikeV2.onThreadComment?.isLiked

                if (liked != null) emit(DataResult.Success(data = liked))
                else emit(DataResult.Error(message = "Unable to like"))
            }
            else emit(DataResult.Error(message = "Error"))
        }
    }
}