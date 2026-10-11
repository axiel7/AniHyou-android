package com.axiel7.anihyou.core.database

import androidx.room3.AutoMigration
import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.axiel7.anihyou.core.database.animenotifcations.AnimeNotificationsDao
import com.axiel7.anihyou.core.database.animenotifcations.AnimeNotificationsEntity
import com.axiel7.anihyou.core.database.customlinks.CustomLinkEntity
import com.axiel7.anihyou.core.database.customlinks.CustomLinksDao
import com.axiel7.anihyou.core.database.savedfilters.SavedFilterEntity
import com.axiel7.anihyou.core.database.savedfilters.SavedFiltersDao

@Database(
    entities = [AnimeNotificationsEntity::class, CustomLinkEntity::class, SavedFilterEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@ColumnTypeConverters(DatabaseConverters::class)
abstract class AnihyouDatabase : RoomDatabase() {
    abstract fun animeNotificationsDao(): AnimeNotificationsDao
    abstract fun customLinksDao(): CustomLinksDao
    abstract fun savedFiltersDao(): SavedFiltersDao
}
