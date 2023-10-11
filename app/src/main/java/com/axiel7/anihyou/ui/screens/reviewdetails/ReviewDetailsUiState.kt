package com.axiel7.anihyou.ui.screens.reviewdetails

import com.axiel7.anihyou.ReviewDetailsQuery
import com.axiel7.anihyou.ui.common.state.UiState

data class ReviewDetailsUiState(
    val details: ReviewDetailsQuery.Review? = null,
    override val error: String? = null,
    override val isLoading: Boolean = true
) : UiState<ReviewDetailsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
