package com.axiel7.anihyou.core.ui.common.state

abstract class PagedUiState : UiState() {
    // We use abstract instead of open to force them to be in the data class declaration
    // so we can use it in the copy() methods
    abstract val page: Int
    abstract val hasNextPage: Boolean

    // These methods are required because we can't have an abstract data class
    // so we need to manually implement the copy() method

    /**
     * `copy(page = value)`
     */
    abstract fun setPage(value: Int): PagedUiState

    /**
     * `copy(hasNextPage = value)`
     */
    abstract fun setHasNextPage(value: Boolean): PagedUiState
}