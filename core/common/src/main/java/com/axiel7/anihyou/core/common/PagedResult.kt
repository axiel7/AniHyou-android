package com.axiel7.anihyou.core.common

sealed interface PagedResult<out T> {
    data object Loading : PagedResult<Nothing>

    data class Error<T>(
        val message: String
    ) : PagedResult<T>

    data class Success<T>(
        val list: List<T>,
        val currentPage: Int?,
        val hasNextPage: Boolean,
    ) : PagedResult<T>
}