package com.axiel7.anihyou.core.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.axiel7.anihyou.core.database.animenotifcations.AnimeNotificationsDao
import com.axiel7.anihyou.core.database.animenotifcations.AnimeNotificationsEntity
import com.axiel7.anihyou.core.database.customlinks.CustomLinkEntity
import com.axiel7.anihyou.core.database.customlinks.CustomLinksDao

@Database(
    entities = [AnimeNotificationsEntity::class, CustomLinkEntity::class],
    version = 1,
)
abstract class AnihyouDatabase : RoomDatabase() {
    abstract fun animeNotificationsDao(): AnimeNotificationsDao
    abstract fun customLinksDao(): CustomLinksDao
}
