package com.axiel7.anihyou.core.domain.repository

import com.axiel7.anihyou.core.database.animenotifcations.AnimeNotifications
import com.axiel7.anihyou.core.database.animenotifcations.AnimeNotificationsDao

class AnimeNotificationsRepository(
    private val dao: AnimeNotificationsDao
) {
    // getter
    suspend fun getAnimeNotificationById(
        animeId: Int
    ) = dao
        .getByAnimeId(
            animeId = animeId
        )

    // changes data
    suspend fun upsertNotification(
        animeId: Int,
        allowStartAiring: Boolean,
        episodeCount: Int?,
        allowAiringEpisode: Boolean,
        allowFinishAiring: Boolean
    ) = dao
        .upsertNotification(
            notification = AnimeNotifications(
                animeId = animeId,
                allowStartAiring = allowStartAiring,
                episodeCount = episodeCount,
                allowNewEpisode = allowAiringEpisode,
                allowFinishAiring = allowFinishAiring
            )
        )

    suspend fun insertNotification(
        animeId: Int,
        allowStartAiring: Boolean,
        episodeCount: Int?,
        allowAiringEpisode: Boolean,
        allowFinishAiring: Boolean
    ) = dao
        .insertNotification(
            notification = AnimeNotifications(
                animeId = animeId,
                allowStartAiring = allowStartAiring,
                episodeCount = episodeCount,
                allowNewEpisode = allowAiringEpisode,
                allowFinishAiring = allowFinishAiring
            )
        )

    suspend fun deleteNotificationById(
        animeId: Int
    ) = dao
        .deleteNotificationById(
            animeId = animeId
        )
}