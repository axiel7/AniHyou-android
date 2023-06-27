package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ReviewDetailsQuery
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import kotlinx.coroutines.flow.flow

object ReviewRepository {

    fun getReviewDetails(reviewId: Int) = flow {
        emit(DataResult.Loading)

        val response = ReviewDetailsQuery(
            reviewId = Optional.present(reviewId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val review = response?.data?.Review
            if (review != null) emit(DataResult.Success(data = review))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun ReviewDetailsQuery.Review.userAcceptance() =
        if (ratingAmount != null && rating != null) (rating * 100) / ratingAmount
        else 0
}