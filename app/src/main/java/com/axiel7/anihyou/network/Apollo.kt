package com.axiel7.anihyou.network

import com.apollographql.apollo3.ApolloClient

val apolloClient = ApolloClient.Builder()
    .serverUrl("https://graphql.anilist.co")
    .build()