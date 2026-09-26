package com.axiel7.anihyou.core.domain

import androidx.room3.Room
import com.axiel7.anihyou.core.database.AnihyouDatabase
import com.axiel7.anihyou.core.domain.repository.AnimeNotificationsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AnihyouDatabase::class.java,
            "anihyou-database"
        ).build()
    }
    
    single { get<AnihyouDatabase>().animeNotificationsDao() }
}
