package com.axiel7.anihyou.wear.ui.screens.login

import com.axiel7.anihyou.core.base.state.UiState

data class LoginUiState(
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
