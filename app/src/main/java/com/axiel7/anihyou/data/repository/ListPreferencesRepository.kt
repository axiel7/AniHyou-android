package com.axiel7.anihyou.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.axiel7.anihyou.di.DataStoreModule.getValue
import com.axiel7.anihyou.di.DataStoreModule.setValue
import com.axiel7.anihyou.ui.common.ItemsPerRow
import com.axiel7.anihyou.ui.common.ListStyle
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ListPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    // list styles
    val generalListStyle = dataStore.getValue(GENERAL_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setGeneralListStyle(value: ListStyle) {
        dataStore.setValue(GENERAL_LIST_STYLE_KEY, value.name)
    }
    val useGeneralListStyle = dataStore.getValue(USE_GENERAL_LIST_STYLE_KEY)
    suspend fun setUseGeneralListStyle(value: Boolean) {
        dataStore.setValue(USE_GENERAL_LIST_STYLE_KEY, value)
    }

    val gridItemsPerRow = dataStore.getValue(GRID_ITEMS_PER_ROW_KEY).map {
        if (it != null) ItemsPerRow.valueOf(it) else null
    }
    suspend fun setGridItemsPerRow(value: ItemsPerRow) {
        dataStore.setValue(GRID_ITEMS_PER_ROW_KEY, value.value)
    }

    // anime separated list styles
    val animeCurrentListStyle = dataStore.getValue(ANIME_CURRENT_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setAnimeCurrentListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_CURRENT_LIST_STYLE_KEY, value.name)
    }
    val animePlanningListStyle = dataStore.getValue(ANIME_PLANNING_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setAnimePlanningListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_PLANNING_LIST_STYLE_KEY, value.name)
    }
    val animeCompletedListStyle = dataStore.getValue(ANIME_COMPLETED_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setAnimeCompletedListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_COMPLETED_LIST_STYLE_KEY, value.name)
    }
    val animePausedListStyle = dataStore.getValue(ANIME_PAUSED_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setAnimePausedListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_PAUSED_LIST_STYLE_KEY, value.name)
    }
    val animeDroppedListStyle = dataStore.getValue(ANIME_DROPPED_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setAnimeDroppedListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_DROPPED_LIST_STYLE_KEY, value.name)
    }
    val animeRepeatingListStyle = dataStore.getValue(ANIME_REPEATING_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setAnimeRepeatingListStyle(value: ListStyle) {
        dataStore.setValue(ANIME_REPEATING_LIST_STYLE_KEY, value.name)
    }

    // manga separated list styles
    val mangaCurrentListStyle = dataStore.getValue(MANGA_CURRENT_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setMangaCurrentListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_CURRENT_LIST_STYLE_KEY, value.name)
    }
    val mangaPlanningListStyle = dataStore.getValue(MANGA_PLANNING_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setMangaPlanningListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_PLANNING_LIST_STYLE_KEY, value.name)
    }
    val mangaCompletedListStyle = dataStore.getValue(MANGA_COMPLETED_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setMangaCompletedListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_COMPLETED_LIST_STYLE_KEY, value.name)
    }
    val mangaPausedListStyle = dataStore.getValue(MANGA_PAUSED_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setMangaPausedListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_PAUSED_LIST_STYLE_KEY, value.name)
    }
    val mangaDroppedListStyle = dataStore.getValue(MANGA_DROPPED_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setMangaDroppedListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_DROPPED_LIST_STYLE_KEY, value.name)
    }
    val mangaRepeatingListStyle = dataStore.getValue(MANGA_REPEATING_LIST_STYLE_KEY).map {
        if (it != null) ListStyle.valueOf(it) else null
    }
    suspend fun setMangaRepeatingListStyle(value: ListStyle) {
        dataStore.setValue(MANGA_REPEATING_LIST_STYLE_KEY, value.name)
    }

    companion object {
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