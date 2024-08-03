package com.axiel7.anihyou.data.repository

import android.util.Log
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloHttpException
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.api.response.PagedResult
import com.axiel7.anihyou.data.api.response.errorString
import com.axiel7.anihyou.fragment.CommonPage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

abstract class BaseNetworkRepository(
    protected val defaultPreferencesRepository: DefaultPreferencesRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    protected fun <D : Operation.Data, R> Flow<ApolloResponse<D>>.asDataResult(
        transform: (D) -> R
    ) = this
        .mapLatest { response ->
            when {
                response.data != null -> {
                    DataResult.Success(transform(response.data!!))
                }

                response.hasErrors() -> {
                    Log.e("AniHyou", "Apollo error: ${response.errorString}")
                    onError(response)
                    DataResult.Error(message = response.errorString)
                }

                response.exception is ApolloHttpException -> {
                    Log.e("AniHyou", "Network error: ${response.errorString}")
                    onError(response)
                    DataResult.Error(message = response.errorString)
                }

                else -> DataResult.Loading
            }
        }

    protected fun <D : Operation.Data> Flow<ApolloResponse<D>>.asDataResult() = asDataResult { it }

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
                    Log.e("AniHyou", "Apollo error: ${response.errorString}")
                    onError(response)
                    PagedResult.Error(message = response.errorString)
                }

                response.exception is ApolloHttpException -> {
                    Log.e("AniHyou", "Network error: ${response.errorString}")
                    onError(response)
                    PagedResult.Error(message = response.errorString)
                }

                else -> PagedResult.Loading
            }
        }

    private suspend fun <D: Operation.Data> onError(response: ApolloResponse<D>) {
        if (response.errors?.any { it.message == "Invalid token" } == true) {
            onInvalidToken()
        }
    }

    private suspend fun onInvalidToken() {
        defaultPreferencesRepository.setAccessToken(null)
    }
}