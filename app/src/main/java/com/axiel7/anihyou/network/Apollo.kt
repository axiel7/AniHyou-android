package com.axiel7.anihyou.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache

val cacheFactory = MemoryCacheFactory(maxSizeBytes = 10 * 1024 * 1024)

val apolloClient = ApolloClient.Builder()
    .serverUrl("https://graphql.anilist.co")
    .normalizedCache(cacheFactory)
    .build()