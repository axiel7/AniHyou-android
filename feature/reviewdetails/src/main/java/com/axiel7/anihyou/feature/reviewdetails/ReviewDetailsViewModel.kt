package com.axiel7.anihyou.feature.reviewdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.domain.repository.ReviewRepository
import com.axiel7.anihyou.core.network.type.ReviewRating
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ReviewDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val reviewRepository: ReviewRepository
) : UiStateViewModel<ReviewDetailsUiState>(), ReviewDetailsEvent {

    private val arguments = savedStateHandle.toRoute<Routes.ReviewDetails>()

    override val initialState = ReviewDetailsUiState()

    override fun rateReview(rating: ReviewRating) {
        reviewRepository.rateReview(arguments.id, rating)
            .onEach { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.update {
                        it.copy(
                            details = it.details?.copy(
                                userRating = result.data?.userRating,
                                rating = result.data?.rating,
                                ratingAmount = result.data?.ratingAmount,
                            )
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    init {
        reviewRepository.getReviewDetails(arguments.id)
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            isLoading = false,
                            details = result.data
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}