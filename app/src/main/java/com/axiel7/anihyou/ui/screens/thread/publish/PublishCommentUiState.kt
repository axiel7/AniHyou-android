package com.axiel7.anihyou.ui.screens.thread.publish

import com.axiel7.anihyou.ui.common.state.UiState

data class PublishCommentUiState(
    val wasPublished: Boolean? = null,
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
