package com.axiel7.anihyou.ui.common.state

/**
 * The inferred type here should be the implementing class itself
 */
interface PagedUiState<T> : UiState<T> {
    override val isLoading: Boolean
    override val error: String?
    val page: Int
    val hasNextPage: Boolean

    // These methods are required because kotlin data class can't inherit from abstract class
    // so we need to manually implement the copy() method

    /**
     * `copy(isLoading = value)`
     */
    override fun setLoading(value: Boolean): T

    /**
     * `copy(error = value)`
     */
    override fun setError(value: String?): T

    /**
     * `copy(page = value)`
     */
    fun setPage(value: Int): T

    /**
     * `copy(hasNextPage = value)`
     */
    fun setHasNextPage(value: Boolean): T
}