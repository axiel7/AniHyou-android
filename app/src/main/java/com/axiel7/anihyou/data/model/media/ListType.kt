package com.axiel7.anihyou.data.model.media

import com.axiel7.anihyou.data.repository.ListPreferencesRepository
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType

data class ListType(
    val status: MediaListStatus,
    val mediaType: MediaType,
) {
    fun stylePreference(
        listPreferencesRepository: ListPreferencesRepository
    ) = when (mediaType) {
        MediaType.ANIME -> when (status) {
            MediaListStatus.CURRENT -> listPreferencesRepository.animeCurrentListStyle
            MediaListStatus.PLANNING -> listPreferencesRepository.animePlanningListStyle
            MediaListStatus.COMPLETED -> listPreferencesRepository.animeCompletedListStyle
            MediaListStatus.DROPPED -> listPreferencesRepository.animeDroppedListStyle
            MediaListStatus.PAUSED -> listPreferencesRepository.animePausedListStyle
            MediaListStatus.REPEATING -> listPreferencesRepository.animeRepeatingListStyle
            else -> null
        }

        MediaType.MANGA -> when (status) {
            MediaListStatus.CURRENT -> listPreferencesRepository.mangaCurrentListStyle
            MediaListStatus.PLANNING -> listPreferencesRepository.mangaPlanningListStyle
            MediaListStatus.COMPLETED -> listPreferencesRepository.mangaCompletedListStyle
            MediaListStatus.DROPPED -> listPreferencesRepository.mangaDroppedListStyle
            MediaListStatus.PAUSED -> listPreferencesRepository.mangaPausedListStyle
            MediaListStatus.REPEATING -> listPreferencesRepository.mangaRepeatingListStyle
            else -> null
        }

        else -> null
    }
}
