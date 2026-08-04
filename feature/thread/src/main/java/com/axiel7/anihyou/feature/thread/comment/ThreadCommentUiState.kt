package com.axiel7.anihyou.feature.thread.comment

import androidx.compose.runtime.Stable
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.model.TranslatorApp

@Stable
data class ThreadCommentUiState(
    val translatorApp: TranslatorApp = TranslatorApp.DEFAULT,
    val isLiked: Boolean = false,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
