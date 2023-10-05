package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
fun <D : Operation.Data, R> Flow<ApolloResponse<D>>.asDataResult(
    transform: (D) -> R
) = this
    .mapLatest { response ->
        if (response.data != null) {
            DataResult.Success(transform(response.data!!))
        } else {
            DataResult.Error(message = response.exception?.localizedMessage ?: "Unknown error")
        }
    }

fun <D : Operation.Data> Flow<ApolloResponse<D>>.asDataResult() = asDataResult { it }

sealed interface DataResult<out T> {
    data object Loading : DataResult<Nothing>

    data class Error<T>(
        val message: String
    ) : DataResult<T>

    data class Success<T>(
        val data: T
    ) : DataResult<T>
}

data class PageResult<out T>(
    val list: List<T>,
    val nextPage: Int?
)