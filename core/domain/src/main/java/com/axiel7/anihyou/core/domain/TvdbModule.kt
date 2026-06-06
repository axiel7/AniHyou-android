package com.axiel7.anihyou.core.domain

import com.axiel7.anihyou.core.base.TVDB_API_KEY
import com.axiel7.anihyou.core.base.TvdbKeyProvider
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import kotlinx.coroutines.flow.first
import org.koin.dsl.module

val tvdbModule = module {
    single<TvdbKeyProvider> {
        {
            get<DefaultPreferencesRepository>().tvdbApiKey.first() ?: TVDB_API_KEY
        }
    }
}
