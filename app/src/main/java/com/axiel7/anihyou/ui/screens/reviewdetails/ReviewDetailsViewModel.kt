package com.axiel7.anihyou.ui.screens.reviewdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.repository.ReviewRepository
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
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
    reviewRepository: ReviewRepository
) : UiStateViewModel<ReviewDetailsUiState>() {

    val reviewId =
        savedStateHandle.getStateFlow<Int?>(REVIEW_ID_ARGUMENT.removeFirstAndLast(), null)

    override val mutableUiState = MutableStateFlow(ReviewDetailsUiState())
    override val uiState = mutableUiState.asStateFlow()

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