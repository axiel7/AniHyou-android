package com.axiel7.anihyou.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.apollographql.apollo3.network.okHttpClient
import com.axiel7.anihyou.common.GlobalVariables
import com.axiel7.anihyou.utils.ANILIST_GRAPHQL_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideApolloClient(
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
            .build()
    }

    class AuthorizationInterceptor(
        private val globalVariables: GlobalVariables,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .apply {
                    globalVariables.accessToken?.let {
                        addHeader("Authorization", "Bearer $it")
                    }
                }
                .build()
            return chain.proceed(request)
        }
    }

    @Singleton
    @Provides
    fun provideAuthorizationInterceptor(
        globalVariables: GlobalVariables
    ): AuthorizationInterceptor {
        return AuthorizationInterceptor(globalVariables)
    }
}