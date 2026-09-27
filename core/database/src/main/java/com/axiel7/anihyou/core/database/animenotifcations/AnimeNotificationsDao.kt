package com.axiel7.anihyou.core.database.animenotifcations

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Upsert

@Dao
interface AnimeNotificationsDao {
    @Insert
    suspend fun insertNotification(notification: AnimeNotificationsEntity)

    @Upsert
    suspend fun upsertNotification(notification: AnimeNotificationsEntity)

    @Query("DELETE FROM anime_notifications WHERE animeId = :animeId")
    suspend fun deleteNotificationById(animeId: Int)

    @Query("SELECT * FROM anime_notifications")
    suspend fun getAllNotifications(): List<AnimeNotificationsEntity>

    @Query("SELECT * FROM anime_notifications WHERE animeId = :animeId")
    suspend fun getByAnimeId(animeId: Int): AnimeNotificationsEntity?
}