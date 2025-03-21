package com.axiel7.anihyou.core.network.api.response

import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Operation

val <D : Operation.Data> ApolloResponse<D>.errorString get() = errors?.joinToString()
    ?: exception?.localizedMessage ?: "Unknown error"