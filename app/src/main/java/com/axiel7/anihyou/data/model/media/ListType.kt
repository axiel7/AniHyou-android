package com.axiel7.anihyou.data.model.media

import com.axiel7.anihyou.App
import com.axiel7.anihyou.data.PreferencesDataStore
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType

data class ListType(
    val status: MediaListStatus,
    val mediaType: MediaType,
) {
    val stylePreferenceKey
        get() = when (status) {
            MediaListStatus.CURRENT ->
                if (mediaType == MediaType.ANIME) PreferencesDataStore.ANIME_CURRENT_LIST_STYLE_PREFERENCE_KEY
                else PreferencesDataStore.MANGA_CURRENT_LIST_STYLE_PREFERENCE_KEY

            MediaListStatus.PLANNING ->
                if (mediaType == MediaType.ANIME) PreferencesDataStore.ANIME_PLANNING_LIST_STYLE_PREFERENCE_KEY
                else PreferencesDataStore.MANGA_PLANNING_LIST_STYLE_PREFERENCE_KEY

            MediaListStatus.COMPLETED ->
                if (mediaType == MediaType.ANIME) PreferencesDataStore.ANIME_COMPLETED_LIST_STYLE_PREFERENCE_KEY
                else PreferencesDataStore.MANGA_COMPLETED_LIST_STYLE_PREFERENCE_KEY

            MediaListStatus.PAUSED ->
                if (mediaType == MediaType.ANIME) PreferencesDataStore.ANIME_PAUSED_LIST_STYLE_PREFERENCE_KEY
                else PreferencesDataStore.MANGA_PAUSED_LIST_STYLE_PREFERENCE_KEY

            MediaListStatus.DROPPED ->
                if (mediaType == MediaType.ANIME) PreferencesDataStore.ANIME_DROPPED_LIST_STYLE_PREFERENCE_KEY
                else PreferencesDataStore.MANGA_DROPPED_LIST_STYLE_PREFERENCE_KEY

            MediaListStatus.REPEATING ->
                if (mediaType == MediaType.ANIME) PreferencesDataStore.ANIME_REPEATING_LIST_STYLE_PREFERENCE_KEY
                else PreferencesDataStore.MANGA_REPEATING_LIST_STYLE_PREFERENCE_KEY

            MediaListStatus.UNKNOWN__ ->
                PreferencesDataStore.GENERAL_LIST_STYLE_PREFERENCE_KEY
        }

    val styleGlobalAppVariable
        get() = when (status) {
            MediaListStatus.CURRENT ->
                if (mediaType == MediaType.ANIME) App.animeCurrentListStyle
                else App.mangaCurrentListStyle

            MediaListStatus.PLANNING ->
                if (mediaType == MediaType.ANIME) App.animePlanningListStyle
                else App.mangaPlanningListStyle

            MediaListStatus.COMPLETED ->
                if (mediaType == MediaType.ANIME) App.animeCompletedListStyle
                else App.mangaCompletedListStyle

            MediaListStatus.PAUSED ->
                if (mediaType == MediaType.ANIME) App.animePausedListStyle
                else App.mangaPausedListStyle

            MediaListStatus.DROPPED ->
                if (mediaType == MediaType.ANIME) App.animeDroppedListStyle
                else App.mangaDroppedListStyle

            MediaListStatus.REPEATING ->
                if (mediaType == MediaType.ANIME) App.animeRepeatingListStyle
                else App.mangaRepeatingListStyle

            MediaListStatus.UNKNOWN__ ->
                App.generalListStyle
        }
}
