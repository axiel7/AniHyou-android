package com.axiel7.anihyou.feature.activitydetails.publish

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.state.UiState

@Immutable
data class PublishActivityUiState(
    val wasPublished: Boolean? = null,
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}