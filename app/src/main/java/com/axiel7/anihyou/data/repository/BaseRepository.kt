package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.axiel7.anihyou.network.apolloClient

object BaseRepository {

    suspend fun <D: Query.Data> Query<D>.tryQuery(
        fetchPolicy: FetchPolicy = FetchPolicy.CacheFirst
    ): ApolloResponse<D>? {
        return try {
            apolloClient
                .query(this)
                .fetchPolicy(fetchPolicy)
                .execute()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun <D: Mutation.Data> Mutation<D>.tryMutation(): ApolloResponse<D>? {
        return try {
            apolloClient.mutation(this).execute()
        } catch (e: Exception) {
            null
        }
    }

    fun <D: Operation.Data> ApolloResponse<D>?.getError() =
        if (this == null || hasErrors() || data == null) {
            this?.errors?.first()?.message ?: "Unknown error"
        }
        else null
}