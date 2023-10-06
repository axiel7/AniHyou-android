package com.axiel7.anihyou.ui.common

/**
 * The inferred type here should be the implementing class itself
 */
interface UiState<T> {
    val isLoading: Boolean
    val error: String?

    fun setLoading(value: Boolean): T
    fun setError(value: String?): T
}