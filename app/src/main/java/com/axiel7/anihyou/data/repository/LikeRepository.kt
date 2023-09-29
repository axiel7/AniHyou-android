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
            if (data != null) emit(DataResult.Success(data = true))
            else emit(DataResult.Error(message = "Error"))
        }
    }
}