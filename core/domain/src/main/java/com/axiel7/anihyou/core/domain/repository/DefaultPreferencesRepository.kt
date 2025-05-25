package com.axiel7.anihyou.core.domain.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.axiel7.anihyou.core.domain.getValue
import com.axiel7.anihyou.core.domain.setValue
import com.axiel7.anihyou.core.model.AppColorMode
import com.axiel7.anihyou.core.model.DefaultTab
import com.axiel7.anihyou.core.model.HomeTab
import com.axiel7.anihyou.core.model.Theme
import com.axiel7.anihyou.core.model.notification.NotificationInterval
import com.axiel7.anihyou.core.model.user.hexColor
import com.axiel7.anihyou.core.network.ViewerOptionsQuery
import com.axiel7.anihyou.core.network.fragment.CommonMediaListOptions
import com.axiel7.anihyou.core.network.fragment.UserInfo
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import com.axiel7.anihyou.core.resources.ColorUtils.colorFromHex
import com.axiel7.anihyou.core.resources.ColorUtils.hexToString
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DefaultPreferencesRepository (
    private val dataStore: DataStore<Preferences>
) {

    // user credentials
    val accessToken = dataStore.getValue(ACCESS_TOKEN_KEY)
    suspend fun setAccessToken(value: String?) {
        dataStore.setValue(ACCESS_TOKEN_KEY, value)
    }

    suspend fun removeAccessToken() {
        dataStore.edit { it.remove(ACCESS_TOKEN_KEY) }
    }

    val isLoggedIn = accessToken.map { it != null }

    val userId = dataStore.getValue(USER_ID_KEY)
    suspend fun setUserId(value: Int) {
        dataStore.setValue(USER_ID_KEY, value)
    }

    suspend fun saveViewerInfo(viewer: ViewerOptionsQuery.Viewer) {
        dataStore.edit {
            it[USER_ID_KEY] = viewer.id
            it[DISPLAY_ADULT_KEY] = viewer.options?.displayAdultContent == true
            it[PROFILE_COLOR_KEY] = viewer.options?.profileColor ?: "#526CFD"
            it[TITLE_LANGUAGE_KEY] =
                viewer.options?.titleLanguage?.rawValue ?: UserTitleLanguage.ROMAJI.rawValue
            viewer.mediaListOptions?.commonMediaListOptions?.let { options ->
                it.saveUserMediaListOption(options)
            }
        }
    }

    suspend fun removeViewerInfo() {
        dataStore.edit {
            it.remove(ACCESS_TOKEN_KEY)
            it.remove(USER_ID_KEY)
            it.remove(DISPLAY_ADULT_KEY)
            it.remove(PROFILE_COLOR_KEY)
            it.remove(SCORE_FORMAT_KEY)
            it.remove(ADVANCED_SCORING_KEY)
            it.remove(TITLE_LANGUAGE_KEY)
            it.remove(NOTIFICATIONS_ENABLED_KEY)
            it.remove(ANIME_SECTION_ORDER_KEY)
            it.remove(ANIME_CUSTOM_LISTS_KEY)
            it.remove(MANGA_SECTION_ORDER_KEY)
            it.remove(MANGA_CUSTOM_LISTS_KEY)
        }
    }

    val displayAdult = dataStore.getValue(DISPLAY_ADULT_KEY)
    suspend fun setDisplayAdult(value: Boolean) {
        dataStore.setValue(DISPLAY_ADULT_KEY, value)
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

    val advancedScoringEnabled = dataStore.getValue(ADVANCED_SCORING_KEY, false)

    val titleLanguage = dataStore.getValue(TITLE_LANGUAGE_KEY).map {
        if (it != null) UserTitleLanguage.safeValueOf(it) else null
    }

    suspend fun saveProfileInfo(userInfo: UserInfo) {
        dataStore.edit {
            val profileColor = userInfo.hexColor()
            it[PROFILE_COLOR_KEY] = profileColor
            if (it[APP_COLOR_MODE_KEY] == AppColorMode.PROFILE.name) {
                it[APP_COLOR_KEY] = profileColor
            }
            it[TITLE_LANGUAGE_KEY] =
                userInfo.options?.titleLanguage?.rawValue ?: UserTitleLanguage.ROMAJI.rawValue
            userInfo.mediaListOptions?.commonMediaListOptions?.let { options ->
                it.saveUserMediaListOption(options)
            }
        }
    }

    private fun MutablePreferences.saveUserMediaListOption(options: CommonMediaListOptions) {
        this[SCORE_FORMAT_KEY] = options.scoreFormat?.rawValue ?: ScoreFormat.POINT_10.rawValue
        this[ADVANCED_SCORING_KEY] = options.animeList?.advancedScoringEnabled == true
        options.animeList?.sectionOrder?.let { sectionOrder ->
            this[ANIME_SECTION_ORDER_KEY] = sectionOrder.joinToString(",")
        }
        options.animeList?.customLists?.let { customLists ->
            if (customLists.isNotEmpty())
                this[ANIME_CUSTOM_LISTS_KEY] = customLists.joinToString(",")
        }
        options.mangaList?.sectionOrder?.let { sectionOrder ->
            this[MANGA_SECTION_ORDER_KEY] = sectionOrder.joinToString(",")
        }
        options.mangaList?.customLists?.let { customLists ->
            if (customLists.isNotEmpty())
                this[MANGA_CUSTOM_LISTS_KEY] = customLists.joinToString(",")
        }
    }

    val animeSectionOrder = dataStore.getValue(ANIME_SECTION_ORDER_KEY).map {
        if (it?.isEmpty() == true) emptyList()
        else it?.split(",")
    }

    val animeCustomLists = dataStore.getValue(ANIME_CUSTOM_LISTS_KEY).map {
        if (it?.isEmpty() == true) emptyList()
        else it?.split(",")
    }

    val animeLists = animeSectionOrder.combine(animeCustomLists) { sectionOrder, customLists ->
        joinSectionOrderWithCustomLists(sectionOrder.orEmpty(), customLists.orEmpty())
    }

    val mangaSectionOrder = dataStore.getValue(MANGA_SECTION_ORDER_KEY).map {
        if (it?.isEmpty() == true) emptyList()
        else it?.split(",")
    }

    val mangaCustomLists = dataStore.getValue(MANGA_CUSTOM_LISTS_KEY).map {
        if (it?.isEmpty() == true) emptyList()
        else it?.split(",")
    }

    val mangaLists = mangaSectionOrder.combine(mangaCustomLists) { sectionOrder, customLists ->
        joinSectionOrderWithCustomLists(sectionOrder.orEmpty(), customLists.orEmpty())
    }

    // for some reason if the user is using the default order
    // custom lists aren't included in `sectionOrder`
    private fun joinSectionOrderWithCustomLists(
        sectionOrder: List<String>,
        customLists: List<String>
    ): List<String> {
        return if (customLists.isEmpty()) sectionOrder
        else {
            if (sectionOrder.containsAll(customLists)) sectionOrder
            else sectionOrder + customLists
        }
    }

    suspend fun saveAnimeCustomLists(value: List<String>) {
        dataStore.setValue(
            key = ANIME_CUSTOM_LISTS_KEY,
            value = value.joinToString(",").takeIf { value.isNotEmpty() }
        )
    }

    suspend fun saveMangaCustomLists(value: List<String>) {
        dataStore.setValue(
            key = MANGA_CUSTOM_LISTS_KEY,
            value = value.joinToString(",").takeIf { value.isNotEmpty() }
        )
    }

    // app
    val theme = dataStore.getValue(key = THEME_KEY, default = Theme.FOLLOW_SYSTEM.name).map {
        Theme.valueOfOrNull(it) ?: Theme.FOLLOW_SYSTEM
    }

    suspend fun setTheme(value: Theme) {
        dataStore.setValue(THEME_KEY, value.name)
    }

    val useBlackColors = dataStore.getValue(key = USE_BLACK_COLORS_KEY, default = false)

    suspend fun setUseBlackColors(value: Boolean) {
        dataStore.setValue(USE_BLACK_COLORS_KEY, value)
    }

    val lastTab = dataStore.getValue(key = LAST_TAB_KEY, default = 0)
    suspend fun setLastTab(value: Int) {
        dataStore.setValue(LAST_TAB_KEY, value)
    }

    val defaultTab =
        dataStore.getValue(key = DEFAULT_TAB_KEY, default = DefaultTab.LAST_USED.ordinal)
            .map { DefaultTab.valueOf(it) }

    suspend fun setDefaultTab(value: DefaultTab) {
        dataStore.setValue(DEFAULT_TAB_KEY, value.ordinal)
    }

    // home
    val defaultHomeTab =
        dataStore.getValue(key = DEFAULT_HOME_TAB_KEY, default = HomeTab.DISCOVER.ordinal)
            .map { HomeTab.valueOf(it) }

    suspend fun setDefaultHomeTab(value: HomeTab) {
        dataStore.setValue(DEFAULT_HOME_TAB_KEY, value.ordinal)
    }

    val airingOnMyList = dataStore.getValue(key = AIRING_ON_MY_LIST_KEY, default = false)
    suspend fun setAiringOnMyList(value: Boolean) {
        dataStore.setValue(AIRING_ON_MY_LIST_KEY, value)
    }

    // calendar
    val calendarOnMyList = dataStore.getValue(key = CALENDAR_ON_MY_LIST_KEY)
    suspend fun setCalendarOnMyList(value: Boolean?) {
        dataStore.setValue(CALENDAR_ON_MY_LIST_KEY, value)
    }

    // notifications
    val isNotificationsEnabled =
        dataStore.getValue(key = NOTIFICATIONS_ENABLED_KEY, default = false)

    suspend fun setNotificationsEnabled(value: Boolean) {
        dataStore.setValue(NOTIFICATIONS_ENABLED_KEY, value)
    }

    val notificationCheckInterval = dataStore.getValue(
        key = NOTIFICATION_INTERVAL_KEY,
        default = NotificationInterval.DAILY.name
    ).map { NotificationInterval.valueOf(it) }

    suspend fun setNotificationCheckInterval(value: NotificationInterval) {
        dataStore.setValue(NOTIFICATION_INTERVAL_KEY, value.name)
    }

    val lastNotificationCreatedAt = dataStore.getValue(LAST_NOTIFICATION_CREATED_AT_KEY)
    suspend fun setLastNotificationCreatedAt(value: Int) {
        dataStore.setValue(LAST_NOTIFICATION_CREATED_AT_KEY, value)
    }

    // custom app color
    val appColorMode =
        dataStore.getValue(key = APP_COLOR_MODE_KEY, default = AppColorMode.DEFAULT.name)
            .map { AppColorMode.valueOf(it) }

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

        private val TITLE_LANGUAGE_KEY = stringPreferencesKey("title_language")
        private val DISPLAY_ADULT_KEY = booleanPreferencesKey("display_adult")
        private val PROFILE_COLOR_KEY = stringPreferencesKey("profile_color")
        private val SCORE_FORMAT_KEY = stringPreferencesKey("score_format")
        private val ADVANCED_SCORING_KEY = booleanPreferencesKey("advanced_scoring")
        private val ANIME_SECTION_ORDER_KEY = stringPreferencesKey("anime_section_order")
        private val MANGA_SECTION_ORDER_KEY = stringPreferencesKey("manga_section_order")
        private val ANIME_CUSTOM_LISTS_KEY = stringPreferencesKey("anime_custom_lists")
        private val MANGA_CUSTOM_LISTS_KEY = stringPreferencesKey("manga_custom_lists")

        private val THEME_KEY = stringPreferencesKey("theme")
        private val USE_BLACK_COLORS_KEY = booleanPreferencesKey("use_black_colors")
        private val LAST_TAB_KEY = intPreferencesKey("last_tab")
        private val DEFAULT_TAB_KEY = intPreferencesKey("default_tab")

        private val DEFAULT_HOME_TAB_KEY = intPreferencesKey("default_home_tab")
        private val AIRING_ON_MY_LIST_KEY = booleanPreferencesKey("airing_on_my_list")
        private val CALENDAR_ON_MY_LIST_KEY = booleanPreferencesKey("calendar_on_my_list")

        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("enabled_notifications")
        private val NOTIFICATION_INTERVAL_KEY = stringPreferencesKey("notification_interval")
        private val LAST_NOTIFICATION_CREATED_AT_KEY =
            intPreferencesKey("last_notification_created_at")

        private val APP_COLOR_MODE_KEY = stringPreferencesKey("app_color_mode")
        private val APP_COLOR_KEY = stringPreferencesKey("app_color")
    }
}