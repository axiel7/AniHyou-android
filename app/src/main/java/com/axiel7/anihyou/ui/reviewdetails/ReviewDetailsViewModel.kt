package com.axiel7.anihyou.ui.reviewdetails

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ReviewDetailsQuery
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ReviewDetailsViewModel : BaseViewModel() {

    var reviewDetails by mutableStateOf<ReviewDetailsQuery.Review?>(null)
    val userAcceptance by derivedStateOf {
        if (reviewDetails?.ratingAmount != null && reviewDetails?.rating != null) {
            (reviewDetails!!.rating!! * 100) / reviewDetails!!.ratingAmount!!
        } else 0
    }

    suspend fun getReviewDetails(reviewId: Int) {
        viewModelScope.launch {
            isLoading = true
            val response = ReviewDetailsQuery(
                reviewId = Optional.present(reviewId)
            ).tryQuery()

            response?.data?.Review?.let { reviewDetails = it }

            isLoading = false
        }
    }
}