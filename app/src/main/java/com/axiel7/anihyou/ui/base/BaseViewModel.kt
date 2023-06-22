package com.axiel7.anihyou.ui.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.refetchPolicy
import com.axiel7.anihyou.network.apolloClient

abstract class BaseViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)

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