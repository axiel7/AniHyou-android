package com.axiel7.anihyou.core.domain.repository

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.exception.ApolloHttpException
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.network.api.response.ErrorResponse
import com.axiel7.anihyou.core.network.api.response.errorString
import com.axiel7.anihyou.core.network.fragment.CommonPage
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
            onError(this)
            DataResult.Error(errorString)
        }

        exception is ApolloHttpException -> {
            val message = onHttpException(this)
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
                    onError(response)
                    PagedResult.Error(message = response.errorString)
                }

                response.exception is ApolloHttpException -> {
                    val message = onHttpException(response)
                    PagedResult.Error(message = message)
                }

                else -> PagedResult.Loading
            }
        }

    private suspend fun <D: Operation.Data> onHttpException(response: ApolloResponse<D>): String {
        val exception = response.exception as ApolloHttpException
        val errorResponse = exception.parseBodyToErrorResponse()
        errorResponse?.let { onError(it) } ?: onError(response)
        return errorResponse?.errors?.joinToString { it.message } ?: response.errorString
    }

    private suspend fun <D: Operation.Data> onError(response: ApolloResponse<D>) {
        if (response.errors?.any { it.message.contains(INVALID_TOKEN_ERROR) } == true) {
            onInvalidToken()
        }
    }

    private suspend fun onError(error: ErrorResponse) {
        if (error.errors.any { it.message.contains(INVALID_TOKEN_ERROR) }) {
            onInvalidToken()
        }
    }

    private suspend fun ApolloHttpException.parseBodyToErrorResponse(): ErrorResponse? {
        val body = body ?: return null
        return withContext(Dispatchers.IO) {
            runCatching {
                val bodyString = body.readUtf8()
                body.close()
                runCatching {
                    Json.decodeFromString<ErrorResponse>(bodyString)
                }.getOrElse {
                    ErrorResponse(errors = listOf(ErrorResponse.Error(message = bodyString)))
                }
            }.getOrNull()
        }
    }

    private suspend fun onInvalidToken() {
        defaultPreferencesRepository.removeAccessToken()
    }

    companion object {
        const val INVALID_TOKEN_ERROR = "Invalid token"
    }
}