package com.axiel7.anihyou.core.base

sealed interface DataResult<out T> {
    data object Loading : DataResult<Nothing>

    data class Error<T>(
        val message: String
    ) : DataResult<T>

    data class Success<T>(
        val data: T
    ) : DataResult<T>
}