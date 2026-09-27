package com.axiel7.anihyou.core.domain

import androidx.room3.Room
import com.axiel7.anihyou.core.database.AnihyouDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AnihyouDatabase::class.java,
            "anihyou-database"
        ).build()
    }
    
    single { get<AnihyouDatabase>().animeNotificationsDao() }
    single { get<AnihyouDatabase>().customLinksDao() }
}
