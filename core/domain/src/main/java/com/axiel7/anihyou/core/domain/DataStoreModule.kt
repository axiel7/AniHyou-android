package com.axiel7.anihyou.core.domain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataStoreModule = module {
    single { providePreferencesDataStore(androidApplication()) }
}

private const val DEFAULT_PREFERENCES = "default"

private fun providePreferencesDataStore(appContext: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(
        produceFile = { appContext.preferencesDataStoreFile(DEFAULT_PREFERENCES) }
    )
}