package com.axiel7.anihyou.ui.screens.reviewdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.repository.ReviewRepository
import com.axiel7.anihyou.type.ReviewRating
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ReviewDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reviewRepository: ReviewRepository
) : UiStateViewModel<ReviewDetailsUiState>(), ReviewDetailsEvent {

    private val arguments = savedStateHandle.toRoute<ReviewDetails>()

    override val initialState = ReviewDetailsUiState()

    override fun rateReview(rating: ReviewRating) {
        reviewRepository.rateReview(arguments.id, rating)
            .onEach { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.update {
                        it.copy(
                            details = it.details?.copy(
                                userRating = result.data.userRating,
                                rating = result.data.rating,
                                ratingAmount = result.data.ratingAmount,
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