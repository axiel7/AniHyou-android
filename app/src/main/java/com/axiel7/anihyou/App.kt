package com.axiel7.anihyou

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.axiel7.anihyou.data.PreferencesDataStore.defaultPreferencesDataStore

class App : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        dataStore = defaultPreferencesDataStore
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .crossfade(true)
            .crossfade(300)
            .error(R.drawable.ic_launcher_foreground)
            .build()
    }

    companion object {
        lateinit var dataStore: DataStore<Preferences>
    }
}