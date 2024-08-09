package com.axiel7.anihyou.data.model.media

import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType

fun CommonMediaListEntry.calculateProgressBarValue(): Float {
    val total = media?.basicMediaDetails?.duration() ?: 0
    return if (total == 0) 0f
    //TODO: volume progress
    else (basicMediaListEntry.progress ?: 0).div(total.toFloat())
}

fun CommonMediaListEntry.isBehind() =
    (basicMediaListEntry.progress ?: 0) < (media?.nextAiringEpisode?.episode?.minus(1) ?: 0)

fun BasicMediaListEntry.isBehind(nextAiringEpisode: Int) = (progress ?: 0) < (nextAiringEpisode - 1)

fun CommonMediaListEntry.episodesBehind() =
    (media?.nextAiringEpisode?.episode?.minus(1) ?: 0) - (basicMediaListEntry.progress ?: 0)

@Suppress("UNCHECKED_CAST")
fun BasicMediaListEntry.advancedScoreNames() = (advancedScores as? LinkedHashMap<String, Any>)?.keys

@Suppress("UNCHECKED_CAST")
fun BasicMediaListEntry.advancedScoresMap() = (advancedScores as? LinkedHashMap<String, Any>)
    ?.mapValues { (it.value as? Number ?: 0).toDouble() } as LinkedHashMap<String, Double>

val exampleBasicMediaListEntry = BasicMediaListEntry(
    __typename = "",
    id = 1,
    mediaId = 1,
    status = MediaListStatus.CURRENT,
    score = 77.0,
    advancedScores = null,
    progress = 999,
    progressVolumes = null,
    repeat = 2,
    startedAt = null,
    completedAt = null,
    private = false,
    hiddenFromStatusLists = false,
    notes = "This is a note",
)

val exampleCommonMediaListEntry = CommonMediaListEntry(
    __typename = "",
    mediaId = 1,
    media = CommonMediaListEntry.Media(
        __typename = "",
        id = 1,
        coverImage = null,
        nextAiringEpisode = CommonMediaListEntry.NextAiringEpisode(
            episode = 3,
            timeUntilAiring = 1203239
        ),
        status = MediaStatus.RELEASING,
        basicMediaDetails = BasicMediaDetails(
            __typename = "",
            id = 1,
            title = BasicMediaDetails.Title(userPreferred = "Kimetsu no Yaiba: Katanakaji no Sato-hen"),
            episodes = 1095,
            chapters = null,
            volumes = null,
            type = MediaType.ANIME
        )
    ),
    id = 1,
    basicMediaListEntry = exampleBasicMediaListEntry
)