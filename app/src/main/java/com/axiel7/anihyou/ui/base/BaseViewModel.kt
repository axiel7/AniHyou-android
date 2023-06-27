package com.axiel7.anihyou.ui.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.network.apolloClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

abstract class BaseViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)

    fun <T> Flow<DataResult<T>>.dataResultStateInViewModel() =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DataResult.Loading)

    fun <T> Flow<PagedResult<T>>.pagedResultStateInViewModel() =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagedResult.Loading)

    //TODO: remove when finish refactor
    suspend fun <D: Query.Data> Query<D>.tryQuery(
        fetchPolicy: FetchPolicy = FetchPolicy.CacheFirst
    ): ApolloResponse<D>? {
        return try {
            val response = apolloClient
                .query(this)
                .fetchPolicy(fetchPolicy)
                .execute()
            if (response.hasErrors()) {
                message = response.errors?.first()?.message
                null
            }
            else response
        } catch (e: Exception) {
            message = e.message
            null
        }
    }

    //TODO: remove when finish refactor
    suspend fun <D: Mutation.Data> Mutation<D>.tryMutation(): ApolloResponse<D>? {
        return try {
            val response = apolloClient.mutation(this).execute()
            if (response.hasErrors()) {
                message = response.errors?.first()?.message
                null
            }
            else response
        } catch (e: Exception) {
            message = e.message
            null
        }
    }
}