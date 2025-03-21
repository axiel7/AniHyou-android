package com.axiel7.anihyou.core.ui.common.state

abstract class UiState {
    // We use abstract instead of open to force them to be in the data class declaration
    // so we can use it in the copy() methods
    abstract val isLoading: Boolean
    abstract val error: String?

    // These methods are required because we can't have an abstract data class
    // so we need to manually implement the copy() method

    /**
     * copy(isLoading = value)
     */
    abstract fun setLoading(value: Boolean): UiState

    /**
     * copy(error = value)
     */
    abstract fun setError(value: String?): UiState

    fun removeError() = setError(null)
}