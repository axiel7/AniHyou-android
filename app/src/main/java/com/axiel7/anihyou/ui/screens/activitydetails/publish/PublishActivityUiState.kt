package com.axiel7.anihyou.ui.screens.activitydetails.publish

import com.axiel7.anihyou.ui.common.UiState

data class PublishActivityUiState(
    val wasPublished: Boolean? = null,
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : UiState<PublishActivityUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}