package com.axiel7.anihyou.core.network

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo.cache.normalized.normalizedCache
import com.apollographql.apollo.network.okHttpClient
import com.axiel7.anihyou.core.base.ANILIST_GRAPHQL_URL
import com.axiel7.anihyou.core.base.X_MAL_CLIENT_ID
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.koin.dsl.module

val networkModule = module {
    single { NetworkVariables() }
    single { provideAuthorizationInterceptor(get()) }
    single { provideApolloClient(get()) }
    single { provideOkHttpClient() }
}

private fun provideApolloClient(
    authorizationInterceptor: AuthorizationInterceptor
): ApolloClient {
    val cacheFactory = MemoryCacheFactory(maxSizeBytes = 10 * 1024 * 1024)

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authorizationInterceptor)
        .build()

    return ApolloClient.Builder()
        .serverUrl(ANILIST_GRAPHQL_URL)
        .okHttpClient(okHttpClient)
        .normalizedCache(cacheFactory)
        .httpExposeErrorBody(true)
        .build()
}

class AuthorizationInterceptor(
    private val networkVariables: NetworkVariables,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .apply {
                networkVariables.accessToken?.let {
                    addHeader("Authorization", "Bearer $it")
                }
            }
            .build()
        return chain.proceed(request)
    }
}

fun provideAuthorizationInterceptor(
    networkVariables: NetworkVariables
): AuthorizationInterceptor {
    return AuthorizationInterceptor(networkVariables)
}

fun provideOkHttpClient(): OkHttpClient {
    val malClientId = System.getProperty("malClientId").orEmpty()
    return OkHttpClient()
        .newBuilder()
        .addInterceptor {
            it.proceed(
                it.request().newBuilder()
                    .addHeader(X_MAL_CLIENT_ID, malClientId)
                    .build()
            )
        }
        .build()
}
