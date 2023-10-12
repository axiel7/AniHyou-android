package com.axiel7.anihyou.ui.common.state

/**
 * The inferred type here should be the implementing class itself
 */
interface UiState<T> {
    val isLoading: Boolean
    val error: String?

    // These methods are required because kotlin data class can't inherit from abstract class
    // so we need to manually implement the copy() method

    /**
     * copy(isLoading = value)
     */
    fun setLoading(value: Boolean): T

    /**
     * copy(error = value)
     */
    fun setError(value: String?): T
}