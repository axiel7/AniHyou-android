package com.axiel7.anihyou.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.network.okHttpClient
import com.axiel7.anihyou.App
import com.axiel7.anihyou.utils.ANILIST_GRAPHQL_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

val cacheFactory = MemoryCacheFactory(maxSizeBytes = 10 * 1024 * 1024)

val apolloClient = ApolloClient.Builder()
    .serverUrl(ANILIST_GRAPHQL_URL)
    .okHttpClient(OkHttpClient.Builder()
        .addInterceptor(AuthorizationInterceptor())
        .build()
    )
    .normalizedCache(cacheFactory)
    .build()

private class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .apply {
                addHeader("Authorization", "Bearer ${App.accessToken}")
            }
            .build()
        return chain.proceed(request)
    }
}