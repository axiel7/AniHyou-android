package com.axiel7.anihyou.feature.stream.di

import com.axiel7.anihyou.feature.stream.data.repository.StreamPreferencesRepository
import com.axiel7.anihyou.feature.stream.data.repository.StreamRepository
import com.axiel7.anihyou.feature.stream.ui.browse.StreamBrowseViewModel
import com.axiel7.anihyou.feature.stream.ui.detail.StreamDetailViewModel
import com.axiel7.anihyou.feature.stream.ui.player.PlayerViewModel
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val streamModule = module {
    // StreamPreferencesRepository shares the app's single DataStore instance
    singleOf(::StreamPreferencesRepository)

    // StreamRepository — uses plain OkHttpClient (no AniList auth header needed)
    single {
        StreamRepository(
            okHttpClient = get<OkHttpClient>(named("plain")),
            mediaRepository = get(),
            baseUrlProvider = { get<StreamPreferencesRepository>().apiBaseUrl.first() },
        )
    }

    viewModelOf(::StreamBrowseViewModel)
    viewModelOf(::StreamDetailViewModel)
    viewModelOf(::PlayerViewModel)
}
