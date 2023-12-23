package com.axiel7.anihyou.ui.screens.reviewdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.repository.ReviewRepository
import com.axiel7.anihyou.type.ReviewRating
import com.axiel7.anihyou.ui.common.navigation.NavArgument
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReviewDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reviewRepository: ReviewRepository
) : UiStateViewModel<ReviewDetailsUiState>(), ReviewDetailsEvent {

    private val reviewId = savedStateHandle.getStateFlow<Int?>(NavArgument.ReviewId.name, null)

    override val mutableUiState = MutableStateFlow(ReviewDetailsUiState())
    override val uiState = mutableUiState.asStateFlow()

    override fun rateReview(rating: ReviewRating) {
        reviewId.value?.let { id ->
            reviewRepository.rateReview(id, rating)
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
    }

    init {
        reviewId
            .filterNotNull()
            .flatMapLatest { reviewId ->
                reviewRepository.getReviewDetails(reviewId)
            }
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