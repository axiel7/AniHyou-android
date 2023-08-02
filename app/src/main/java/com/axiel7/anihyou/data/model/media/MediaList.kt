package com.axiel7.anihyou.data.model.media

import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType

fun UserMediaListQuery.MediaList.calculateProgressBarValue(): Float {
    val total = media?.basicMediaDetails?.duration() ?: 0
    return if (total == 0) 0f
    //TODO: volume progress
    else (basicMediaListEntry.progress ?: 0).div(total.toFloat())
}

fun BasicMediaListEntry.isBehind(nextAiringEpisode: Int) = (progress ?: 0) < (nextAiringEpisode - 1)

val exampleMediaList = UserMediaListQuery.MediaList(
    __typename = "",
    id = 1,
    mediaId = 1,
    media = UserMediaListQuery.Media(
        __typename = "",
        id = 1,
        coverImage = null,
        nextAiringEpisode = UserMediaListQuery.NextAiringEpisode(
            episode = 3,
            timeUntilAiring = 1203239
        ),
        status = MediaStatus.RELEASING,
        basicMediaDetails = BasicMediaDetails(
            __typename = "",
            id = 1,
            title = BasicMediaDetails.Title(userPreferred = "Kimetsu no Yaiba: Katanakaji no Sato-hen"),
            episodes = 12,
            chapters = null,
            volumes = null,
            type = MediaType.ANIME
        )
    ),
    basicMediaListEntry = BasicMediaListEntry(
        __typename = "",
        id = 1,
        mediaId = 1,
        status = MediaListStatus.CURRENT,
        score = 77.0,
        progress = 3,
        progressVolumes = null,
        repeat = null,
        startedAt = null,
        completedAt = null,
        private = false,
        notes = null,
    )
)