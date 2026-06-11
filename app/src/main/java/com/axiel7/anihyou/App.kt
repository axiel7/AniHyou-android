package com.axiel7.anihyou

import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.request.crossfade
import com.axiel7.anihyou.core.domain.dataStoreModule
import com.axiel7.anihyou.core.domain.repositoryModule
import com.axiel7.anihyou.core.network.apiModule
import com.axiel7.anihyou.core.network.networkModule
import com.axiel7.anihyou.feature.worker.workerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
class App : Application(), KoinComponent, KoinStartup, SingletonImageLoader.Factory {

    override fun onKoinStartup() = koinConfiguration {
        if (BuildConfig.DEBUG) {
            androidLogger()
        }
        androidContext(this@App)
        workManagerFactory()
        modules(
            dataStoreModule,
            networkModule,
            apiModule,
            repositoryModule,
            viewModelModule,
            workerModule,
        )
    }

    override fun newImageLoader(context: PlatformContext) =
        ImageLoader.Builder(this)
            .components {
                if (SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, percent = 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.15)
                    .build()
            }
            .crossfade(true)
            .build()
}