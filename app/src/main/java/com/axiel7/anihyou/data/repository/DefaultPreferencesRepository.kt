package com.axiel7.anihyou.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.axiel7.anihyou.ViewerIdQuery
import com.axiel7.anihyou.data.model.notification.NotificationInterval
import com.axiel7.anihyou.di.DataStoreModule.getValue
import com.axiel7.anihyou.di.DataStoreModule.setValue
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.AppColorMode
import com.axiel7.anihyou.ui.common.Theme
import com.axiel7.anihyou.ui.screens.home.HomeTab
import com.axiel7.anihyou.utils.ColorUtils.colorFromHex
import com.axiel7.anihyou.utils.ColorUtils.hexToString
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    // user credentials
    val accessToken = dataStore.getValue(ACCESS_TOKEN_KEY)
    suspend fun setAccessToken(value: String) {
        dataStore.setValue(ACCESS_TOKEN_KEY, value)
    }
    val userId = dataStore.getValue(USER_ID_KEY)
    suspend fun setUserId(value: Int) {
        dataStore.setValue(USER_ID_KEY, value)
    }

    suspend fun saveViewerInfo(viewer: ViewerIdQuery.Viewer) {
        dataStore.edit {
            it[USER_ID_KEY] = viewer.id
            it[PROFILE_COLOR_KEY] = viewer.options?.profileColor ?: "#526CFD"
            it[SCORE_FORMAT_KEY] =
                viewer.mediaListOptions?.scoreFormat?.name ?: "POINT_10"
        }
    }

    suspend fun removeViewerInfo() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN_KEY)
            it.remove(USER_ID_KEY)
            it.remove(PROFILE_COLOR_KEY)
            it.remove(SCORE_FORMAT_KEY)
            it.remove(NOTIFICATIONS_ENABLED_KEY)
        }
    }

    // profile info
    val profileColor = dataStore.getValue(PROFILE_COLOR_KEY).map {
        if (it != null) colorFromHex(it) else null
    }
    suspend fun setProfileColor(value: String) {
        dataStore.setValue(PROFILE_COLOR_KEY, value)
    }
    val scoreFormat = dataStore.getValue(SCORE_FORMAT_KEY).map {
        if (it != null) ScoreFormat.safeValueOf(it) else null
    }
    suspend fun setScoreFormat(value: ScoreFormat) {
        dataStore.setValue(SCORE_FORMAT_KEY, value.name)
    }

    // app
    val theme = dataStore.getValue(THEME_KEY).map {
        if (it != null) Theme.valueOf(it) else null
    }
    suspend fun setTheme(value: Theme) {
        dataStore.setValue(THEME_KEY, value.name)
    }
    val lastTab = dataStore.getValue(LAST_TAB_KEY)
    suspend fun setLastTab(value: Int) {
        dataStore.setValue(LAST_TAB_KEY, value)
    }

    // list sort
    val animeListSort = dataStore.getValue(ANIME_LIST_SORT_KEY).map {
        if (it != null) MediaListSort.safeValueOf(it) else null
    }
    suspend fun setAnimeListSort(value: MediaListSort) {
        dataStore.setValue(ANIME_LIST_SORT_KEY, value.rawValue)
    }
    val mangaListSort = dataStore.getValue(MANGA_LIST_SORT_KEY).map {
        if (it != null) MediaListSort.safeValueOf(it) else null
    }
    suspend fun setMangaListSort(value: MediaListSort) {
        dataStore.setValue(MANGA_LIST_SORT_KEY, value.rawValue)
    }

    // home
    val defaultHomeTab = dataStore.getValue(DEFAULT_HOME_TAB_KEY).map {
        if (it != null) HomeTab.valueOf(it) else null
    }
    suspend fun setDefaultHomeTab(value: HomeTab) {
        dataStore.setValue(DEFAULT_HOME_TAB_KEY, value.index)
    }
    val airingOnMyList = dataStore.getValue(AIRING_ON_MY_LIST_KEY)
    suspend fun setAiringOnMyList(value: Boolean) {
        dataStore.setValue(AIRING_ON_MY_LIST_KEY, value)
    }

    // notifications
    val isNotificationsEnabled = dataStore.getValue(NOTIFICATIONS_ENABLED_KEY)
    suspend fun setNotificationsEnabled(value: Boolean) {
        dataStore.setValue(NOTIFICATIONS_ENABLED_KEY, value)
    }
    val notificationCheckInterval = dataStore.getValue(NOTIFICATION_INTERVAL_KEY).map {
        if (it != null) NotificationInterval.valueOf(it) else NotificationInterval.DAILY
    }
    suspend fun setNotificationCheckInterval(value: NotificationInterval) {
        dataStore.setValue(NOTIFICATION_INTERVAL_KEY, value.name)
    }
    val lastNotificationCreatedAt = dataStore.getValue(LAST_NOTIFICATION_CREATED_AT_KEY)
    suspend fun setLastNotificationCreatedAt(value: Int) {
        dataStore.setValue(LAST_NOTIFICATION_CREATED_AT_KEY, value)
    }

    // custom app color
    val appColorMode = dataStore.getValue(APP_COLOR_MODE_KEY).map {
        if (it != null) AppColorMode.valueOf(it) else null
    }
    suspend fun setAppColorMode(value: AppColorMode) {
        dataStore.setValue(APP_COLOR_MODE_KEY, value.name)
    }
    val appColor = dataStore.getValue(APP_COLOR_KEY).map {
        if (it != null) colorFromHex(it) else null
    }
    suspend fun setAppColor(value: Color?) {
        dataStore.setValue(APP_COLOR_KEY, value?.toArgb()?.hexToString())
    }

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val USER_ID_KEY = intPreferencesKey("user_id")

        private val PROFILE_COLOR_KEY = stringPreferencesKey("profile_color")
        private val SCORE_FORMAT_KEY = stringPreferencesKey("score_format")

        private val THEME_KEY = stringPreferencesKey("theme")
        private val LAST_TAB_KEY = intPreferencesKey("last_tab")

        private val ANIME_LIST_SORT_KEY = stringPreferencesKey("anime_list_sort")
        private val MANGA_LIST_SORT_KEY = stringPreferencesKey("manga_list_sort")

        private val DEFAULT_HOME_TAB_KEY = intPreferencesKey("default_home_tab")
        private val AIRING_ON_MY_LIST_KEY = booleanPreferencesKey("airing_on_my_list")

        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("enabled_notifications")
        private val NOTIFICATION_INTERVAL_KEY = stringPreferencesKey("notification_interval")
        private val LAST_NOTIFICATION_CREATED_AT_KEY =
            intPreferencesKey("last_notification_created_at")

        private val APP_COLOR_MODE_KEY = stringPreferencesKey("app_color_mode")
        private val APP_COLOR_KEY = stringPreferencesKey("app_color")
    }
}