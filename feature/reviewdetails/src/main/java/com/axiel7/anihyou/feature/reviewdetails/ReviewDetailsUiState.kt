package com.axiel7.anihyou.feature.reviewdetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.network.ReviewDetailsQuery
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.model.TranslatorApp

@Immutable
data class ReviewDetailsUiState(
    val translatorApp: TranslatorApp = TranslatorApp.DEFAULT,
    val details: ReviewDetailsQuery.Review? = null,
    override val error: String? = null,
    override val isLoading: Boolean = true
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
