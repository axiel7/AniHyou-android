package com.axiel7.anihyou.core.streaming.di

import com.axiel7.anihyou.core.streaming.api.AllAnimeProvider
import com.axiel7.anihyou.core.streaming.api.StreamingProvider
import com.axiel7.anihyou.core.streaming.model.StreamingSource
import com.axiel7.anihyou.core.streaming.repository.StreamingPreferencesRepository
import com.axiel7.anihyou.core.streaming.repository.StreamingRepository
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val streamingModule = module {
    single<OkHttpClient> { OkHttpClient() }
    single<Map<StreamingSource, StreamingProvider>> {
        mapOf(StreamingSource.ALL_ANIME to AllAnimeProvider(get()))
    }
    singleOf(::StreamingPreferencesRepository)
    singleOf(::StreamingRepository)
}
