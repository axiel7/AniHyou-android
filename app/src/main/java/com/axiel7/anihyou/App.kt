package com.axiel7.anihyou

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory

class App : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .crossfade(300)
            .error(R.drawable.ic_launcher_foreground)
            .build()
    }
}