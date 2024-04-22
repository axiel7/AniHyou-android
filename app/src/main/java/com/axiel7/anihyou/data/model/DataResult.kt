package com.axiel7.anihyou.data.model

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
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

            response.exception != null -> {
                Log.e("AniHyou", "Apollo error", response.exception)
                DataResult.Error(
                    message = response.exception?.localizedMessage
                        ?: "Exception: ${response.exception}"
                )
            }

            else -> DataResult.Loading
        }

    }

fun <D : Operation.Data> Flow<ApolloResponse<D>>.asDataResult() = asDataResult { it }