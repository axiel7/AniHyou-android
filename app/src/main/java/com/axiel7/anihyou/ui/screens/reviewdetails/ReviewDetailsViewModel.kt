package com.axiel7.anihyou.ui.screens.reviewdetails

import com.axiel7.anihyou.data.repository.ReviewRepository
import com.axiel7.anihyou.ui.base.BaseViewModel

class ReviewDetailsViewModel(
    reviewId: Int
) : BaseViewModel() {

    val reviewDetails = ReviewRepository.getReviewDetails(reviewId).dataResultStateInViewModel()

}