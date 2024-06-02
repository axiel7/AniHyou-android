package com.axiel7.anihyou.data.model

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloHttpException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

sealed interface DataResult<out T> {
    data object Loading : DataResult<Nothing>

    data class Error<T>(
        val message: String
    ) : DataResult<T>

    data class Success<T>(
        val data: T
    ) : DataResult<T>
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <D : Operation.Data, R> Flow<ApolloResponse<D>>.asDataResult(
    transform: (D) -> R
) = this
    .mapLatest { response ->
        when {
            response.data != null -> {
                DataResult.Success(transform(response.data!!))
            }

            response.hasErrors() -> {
                Log.e("AniHyou", "Apollo error: ${response.errors?.joinToString()}")
                DataResult.Error(
                    message = response.errors?.joinToString() ?: "Unknown error"
                )
            }

            response.exception is ApolloHttpException ->
                DataResult.Error(message = response.exception?.localizedMessage ?: "Unknown error")

            else -> DataResult.Loading
        }

    }

fun <D : Operation.Data> Flow<ApolloResponse<D>>.asDataResult() = asDataResult { it }

fun <D : Operation.Data, R> ApolloResponse<D>.asDataResult(
    transform: (D) -> R
) = when {
    data != null -> DataResult.Success(transform(data!!))

    hasErrors() -> {
        Log.e("AniHyou", "Apollo error: ${errors?.joinToString()}")
        DataResult.Error(
            message = errors?.joinToString() ?: "Unknown error"
        )
    }

    else -> DataResult.Error(message = exception?.localizedMessage ?: "Unknown error")
}

fun <D : Operation.Data> ApolloResponse<D>.asDataResult() = asDataResult { it }