package com.axiel7.anihyou.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.axiel7.anihyou.di.DataStoreModule.getValue
import com.axiel7.anihyou.di.DataStoreModule.setValue
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.ui.common.ItemsPerRow
import com.axiel7.anihyou.ui.common.ListStyle
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // default status
    val animeListStatus = dataStore.getValue(
        key = ANIME_LIST_STATUS_KEY,
        default = MediaListStatus.CURRENT.name
    ).map { MediaListStatus.valueOf(it) }

    suspend fun setAnimeListStatus(value: MediaListStatus) {
        dataStore.setValue(ANIME_LIST_STATUS_KEY, value.name)
    }

    val mangaListStatus = dataStore.getValue(
        key = MANGA_LIST_STATUS_KEY,
        default = MediaListStatus.CURRENT.name
    ).map { MediaListStatus.valueOf(it) }

    suspend fun setMangaListStatus(value: MediaListStatus) {
        dataStore.setValue(MANGA_LIST_STATUS_KEY, value.name)
    }

    // list sort
    val animeListSort = dataStore.getValue(
        key = ANIME_LIST_SORT_KEY,
        default = MediaListSort.UPDATED_TIME_DESC.rawValue
    ).map { MediaListSort.safeValueOf(it) }

    suspend fun setAnimeListSort(value: MediaListSort) {
        dataStore.setValue(ANIME_LIST_SORT_KEY, value.rawValue)
    }

    val mangaListSort = dataStore.getValue(
        key = MANGA_LIST_SORT_KEY,
        default = MediaListSort.UPDATED_TIME_DESC.rawValue
    ).map { MediaListSort.safeValueOf(it) }

    suspend fun setMangaListSort(value: MediaListSort) {
        dataStore.setValue(MANGA_LIST_SORT_KEY, value.rawValue)
    }

    // list styles
    val generalListStyle = dataStore.getValue(
        GENERAL_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setGeneralListStyle(value: ListStyle) {
        dataStore.setValue(GENERAL_LIST_STYLE_KEY, value.name)
    }

    val useGeneralListStyle = dataStore.getValue(key = USE_GENERAL_LIST_STYLE_KEY, default = true)
    suspend fun setUseGeneralListStyle(value: Boolean) {
        dataStore.setValue(USE_GENERAL_LIST_STYLE_KEY, value)
    }

    val gridItemsPerRow = dataStore.getValue(
        key = GRID_ITEMS_PER_ROW_KEY,
        default = ItemsPerRow.DEFAULT.value
    ).map { ItemsPerRow.valueOf(it) }

    suspend fun setGridItemsPerRow(value: ItemsPerRow) {
        dataStore.setValue(GRID_ITEMS_PER_ROW_KEY, value.value)
    }

    // anime separated list styles
    val animeCurrentListStyle = dataStore.getValue(
        key = ANIME_CURRENT_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setAnimeCurrentListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_CURRENT_LIST_STYLE_KEY, value.name)
    }

    val animePlanningListStyle = dataStore.getValue(
        key = ANIME_PLANNING_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setAnimePlanningListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_PLANNING_LIST_STYLE_KEY, value.name)
    }

    val animeCompletedListStyle = dataStore.getValue(
        key = ANIME_COMPLETED_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setAnimeCompletedListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_COMPLETED_LIST_STYLE_KEY, value.name)
    }

    val animePausedListStyle = dataStore.getValue(
        key = ANIME_PAUSED_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setAnimePausedListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_PAUSED_LIST_STYLE_KEY, value.name)
    }

    val animeDroppedListStyle = dataStore.getValue(
        key = ANIME_DROPPED_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setAnimeDroppedListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_DROPPED_LIST_STYLE_KEY, value.name)
    }

    val animeRepeatingListStyle = dataStore.getValue(
        key = ANIME_REPEATING_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setAnimeRepeatingListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_REPEATING_LIST_STYLE_KEY, value.name)
    }

    // manga separated list styles
    val mangaCurrentListStyle = dataStore.getValue(
        key = MANGA_CURRENT_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setMangaCurrentListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_CURRENT_LIST_STYLE_KEY, value.name)
    }

    val mangaPlanningListStyle = dataStore.getValue(
        key = MANGA_PLANNING_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setMangaPlanningListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_PLANNING_LIST_STYLE_KEY, value.name)
    }

    val mangaCompletedListStyle = dataStore.getValue(
        key = MANGA_COMPLETED_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setMangaCompletedListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_COMPLETED_LIST_STYLE_KEY, value.name)
    }

    val mangaPausedListStyle = dataStore.getValue(
        key = MANGA_PAUSED_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setMangaPausedListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_PAUSED_LIST_STYLE_KEY, value.name)
    }

    val mangaDroppedListStyle = dataStore.getValue(
        key = MANGA_DROPPED_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setMangaDroppedListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_DROPPED_LIST_STYLE_KEY, value.name)
    }

    val mangaRepeatingListStyle = dataStore.getValue(
        key = MANGA_REPEATING_LIST_STYLE_KEY,
        default = ListStyle.STANDARD.name
    ).map { ListStyle.valueOf(it) }

    suspend fun setMangaRepeatingListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_REPEATING_LIST_STYLE_KEY, value.name)
    }

    companion object {
        private val ANIME_LIST_STATUS_KEY = stringPreferencesKey("anime_list_status")
        private val MANGA_LIST_STATUS_KEY = stringPreferencesKey("manga_list_status")

        private val ANIME_LIST_SORT_KEY = stringPreferencesKey("anime_list_sort")
        private val MANGA_LIST_SORT_KEY = stringPreferencesKey("manga_list_sort")

        private val GENERAL_LIST_STYLE_KEY = stringPreferencesKey("list_display_mode")
        private val USE_GENERAL_LIST_STYLE_KEY = booleanPreferencesKey("use_general_list_style")

        private val GRID_ITEMS_PER_ROW_KEY = intPreferencesKey("grid_items_per_row")

        private val ANIME_CURRENT_LIST_STYLE_KEY = stringPreferencesKey("anime_current_list_style")
        private val ANIME_PLANNING_LIST_STYLE_KEY =
            stringPreferencesKey("anime_planning_list_style")
        private val ANIME_COMPLETED_LIST_STYLE_KEY =
            stringPreferencesKey("anime_completed_list_style")
        private val ANIME_PAUSED_LIST_STYLE_KEY = stringPreferencesKey("anime_paused_list_style")
        private val ANIME_DROPPED_LIST_STYLE_KEY = stringPreferencesKey("anime_dropped_list_style")
        private val ANIME_REPEATING_LIST_STYLE_KEY =
            stringPreferencesKey("anime_repeating_list_style")

        private val MANGA_CURRENT_LIST_STYLE_KEY = stringPreferencesKey("manga_current_list_style")
        private val MANGA_PLANNING_LIST_STYLE_KEY =
            stringPreferencesKey("manga_planning_list_style")
        private val MANGA_COMPLETED_LIST_STYLE_KEY =
            stringPreferencesKey("manga_completed_list_style")
        private val MANGA_PAUSED_LIST_STYLE_KEY = stringPreferencesKey("manga_paused_list_style")
        private val MANGA_DROPPED_LIST_STYLE_KEY = stringPreferencesKey("manga_dropped_list_style")
        private val MANGA_REPEATING_LIST_STYLE_KEY =
            stringPreferencesKey("anime_repeating_list_style")
    }
}