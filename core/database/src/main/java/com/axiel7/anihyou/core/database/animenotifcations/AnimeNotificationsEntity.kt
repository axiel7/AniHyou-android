package com.axiel7.anihyou.core.database.animenotifcations

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "anime_notifications")
data class AnimeNotifications(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "animeId")
    val animeId: Int,

    @ColumnInfo(name = "allowStartAiring", defaultValue = "1")
    val allowStartAiring: Boolean = true,

    @ColumnInfo(name = "episodeCount", defaultValue = "null")
    val episodeCount: Int? = null,

    @ColumnInfo(name = "allowNewEpisode", defaultValue = "1")
    val allowNewEpisode: Boolean = true,

    @ColumnInfo(name = "allowFinishAiring", defaultValue = "0")
    val allowFinishAiring: Boolean = false,
)