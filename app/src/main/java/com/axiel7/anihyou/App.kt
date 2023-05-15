package com.axiel7.anihyou

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.axiel7.anihyou.data.PreferencesDataStore.defaultPreferencesDataStore
import com.axiel7.anihyou.type.MediaListSort

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        dataStore = defaultPreferencesDataStore
    }

    companion object {
        lateinit var dataStore: DataStore<Preferences>
        var animeListSort = MediaListSort.UPDATED_TIME_DESC.rawValue
        var mangaListSort = MediaListSort.UPDATED_TIME_DESC.rawValue
    }
}