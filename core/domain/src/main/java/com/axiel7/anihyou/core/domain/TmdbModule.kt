package com.axiel7.anihyou.core.domain

import com.axiel7.anihyou.core.base.TmdbKeyProvider
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.network.api.TmdbApi
import kotlinx.coroutines.flow.first
import org.koin.core.qualifier.named
import org.koin.dsl.module

val tmdbModule = module {
    single<TmdbKeyProvider> {
        {
            get<DefaultPreferencesRepository>().tmdbApiKey.first()
        }
    }
    single { TmdbApi(get(named("plain")), get()) }
}

