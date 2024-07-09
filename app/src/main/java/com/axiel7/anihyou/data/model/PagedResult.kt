package com.axiel7.anihyou.data.model

import android.util.Log
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloHttpException
import com.axiel7.anihyou.fragment.CommonPage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

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

@OptIn(ExperimentalCoroutinesApi::class)
fun <D : Operation.Data, R> Flow<ApolloResponse<D>>.asPagedResult(
    page: (D) -> CommonPage?,
    transform: (D) -> List<R>
) = this
    .mapLatest { response ->
        when {
            response.data != null -> {
                val commonPage = page(response.data!!)
                PagedResult.Success(
                    list = transform(response.data!!),
                    currentPage = commonPage?.currentPage,
                    hasNextPage = commonPage?.hasNextPage == true,
                )
            }

            response.hasErrors() -> {
                Log.e("AniHyou", "Apollo error: ${response.errors?.joinToString()}")
                PagedResult.Error(
                    message = response.errors?.joinToString() ?: "Unknown error"
                )
            }

            response.exception is ApolloHttpException ->
                PagedResult.Error(message = response.exception?.localizedMessage ?: "Unknown error")

            else -> PagedResult.Loading
        }
    }