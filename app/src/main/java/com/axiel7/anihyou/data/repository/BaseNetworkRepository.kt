package com.axiel7.anihyou.data.repository

import android.util.Log
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloHttpException
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.api.response.PagedResult
import com.axiel7.anihyou.data.api.response.errorString
import com.axiel7.anihyou.data.model.ErrorResponse
import com.axiel7.anihyou.fragment.CommonPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

abstract class BaseNetworkRepository(
    protected val defaultPreferencesRepository: DefaultPreferencesRepository
) {
    protected suspend fun <D : Operation.Data, R> ApolloResponse<D>.asDataResult(
        transform: (D) -> R
    ) = when {
        data != null -> DataResult.Success(transform(data!!))

        hasErrors() -> {
            Log.e("AniHyou", "Apollo error: $errorString")
            onError(this)
            DataResult.Error(errorString)
        }

        exception is ApolloHttpException -> {
            val exception = exception as ApolloHttpException
            val errorResponse = exception.parseBodyToErrorResponse()
            errorResponse?.let { onError(it) } ?: onError(this)
            val message = errorResponse?.errors?.joinToString { it.message } ?: errorString
            Log.e("AniHyou", "Network error: $message")
            DataResult.Error(message = message)
        }

        else -> DataResult.Loading
    }

    protected suspend fun <D : Operation.Data, R> ApolloResponse<D>.asDataResult() =
        this.asDataResult { it }

    @OptIn(ExperimentalCoroutinesApi::class)
    protected fun <D : Operation.Data, R> Flow<ApolloResponse<D>>.asDataResult(
        transform: (D) -> R
    ) = mapLatest { it.asDataResult(transform) }

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
                    val exception = response.exception as ApolloHttpException
                    val errorResponse = exception.parseBodyToErrorResponse()
                    errorResponse?.let { onError(it) } ?: onError(response)
                    val message = errorResponse?.errors?.joinToString { it.message } ?: response.errorString
                    Log.e("AniHyou", "Network error: $message")
                    PagedResult.Error(message = message)
                }

                else -> PagedResult.Loading
            }
        }

    private suspend fun <D: Operation.Data> onError(response: ApolloResponse<D>) {
        if (response.errors?.any { it.message == "Invalid token" } == true) {
            onInvalidToken()
        }
    }

    private suspend fun onError(error: ErrorResponse) {
        if (error.errors.any { it.message == "Invalid token" }) {
            onInvalidToken()
        }
    }

    private suspend fun ApolloHttpException.parseBodyToErrorResponse(): ErrorResponse? {
        val body = body ?: return null
        return withContext(Dispatchers.IO) {
            runCatching {
                val bodyString = body.readUtf8()
                body.close()
                Json.decodeFromString<ErrorResponse>(bodyString)
            }.getOrNull()
        }
    }

    private suspend fun onInvalidToken() {
        defaultPreferencesRepository.setAccessToken(null)
    }
}