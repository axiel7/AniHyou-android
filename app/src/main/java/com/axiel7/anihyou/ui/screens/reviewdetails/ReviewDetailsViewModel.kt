package com.axiel7.anihyou.ui.screens.reviewdetails

import com.axiel7.anihyou.data.repository.ReviewRepository
import com.axiel7.anihyou.ui.common.UiStateViewModel

class ReviewDetailsViewModel(
    reviewId: Int
) : UiStateViewModel() {

    val reviewDetails = ReviewRepository.getReviewDetails(reviewId).dataResultStateInViewModel()

}