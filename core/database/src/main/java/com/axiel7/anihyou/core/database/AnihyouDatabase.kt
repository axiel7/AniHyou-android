package com.axiel7.anihyou.core.database

import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.axiel7.anihyou.core.database.animenotifcations.AnimeNotifications
import com.axiel7.anihyou.core.database.animenotifcations.AnimeNotificationsDao

@Database(
    entities = [AnimeNotifications::class],
    version = 1,
)
abstract class AnihyouDatabase : RoomDatabase() {
    abstract fun animeNotificationsDao(): AnimeNotificationsDao
}
