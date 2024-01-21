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
import com.axiel7.anihyou.data.model.user.hexColor
import com.axiel7.anihyou.di.DataStoreModule.getValue
import com.axiel7.anihyou.di.DataStoreModule.setValue
import com.axiel7.anihyou.fragment.UserInfo
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.type.UserTitleLanguage
import com.axiel7.anihyou.ui.common.AppColorMode
import com.axiel7.anihyou.ui.common.Theme
import com.axiel7.anihyou.ui.screens.home.HomeTab
import com.axiel7.anihyou.utils.ColorUtils.colorFromHex
import com.axiel7.anihyou.utils.ColorUtils.hexToString
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    // user credentials
    val accessToken = dataStore.getValue(ACCESS_TOKEN_KEY)
    suspend fun setAccessToken(value: String) {
        dataStore.setValue(ACCESS_TOKEN_KEY, value)
    }

    val isLoggedIn = accessToken.map { it != null }

    val userId = dataStore.getValue(USER_ID_KEY)
    suspend fun setUserId(value: Int) {
        dataStore.setValue(USER_ID_KEY, value)
    }

    suspend fun saveViewerInfo(viewer: ViewerIdQuery.Viewer) {
        dataStore.edit {
            it[USER_ID_KEY] = viewer.id
            it[DISPLAY_ADULT_KEY] = viewer.options?.displayAdultContent == true
            it[PROFILE_COLOR_KEY] = viewer.options?.profileColor ?: "#526CFD"
            it[SCORE_FORMAT_KEY] =
                viewer.mediaListOptions?.commonMediaListOptions?.scoreFormat?.rawValue
                    ?: ScoreFormat.POINT_10.rawValue
            it[ADVANCED_SCORING_KEY] =
                viewer.mediaListOptions?.commonMediaListOptions?.animeList?.advancedScoringEnabled == true
            it[TITLE_LANGUAGE_KEY] =
                viewer.options?.titleLanguage?.rawValue ?: UserTitleLanguage.ROMAJI.rawValue
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
            it[SCORE_FORMAT_KEY] =
                userInfo.mediaListOptions?.commonMediaListOptions?.scoreFormat?.rawValue
                    ?: ScoreFormat.POINT_10.rawValue
            it[ADVANCED_SCORING_KEY] =
                userInfo.mediaListOptions?.commonMediaListOptions?.animeList?.advancedScoringEnabled == true
            it[TITLE_LANGUAGE_KEY] =
                userInfo.options?.titleLanguage?.rawValue ?: UserTitleLanguage.ROMAJI.rawValue
            if (it[APP_COLOR_MODE_KEY] == AppColorMode.PROFILE.name) {
                it[APP_COLOR_KEY] = profileColor
            }
        }
    }

    // app
    val theme = dataStore.getValue(key = THEME_KEY, default = Theme.FOLLOW_SYSTEM.name).map {
        Theme.valueOfOrNull(it) ?: Theme.FOLLOW_SYSTEM
    }

    suspend fun setTheme(value: Theme) {
        dataStore.setValue(THEME_KEY, value.name)
    }

    val lastTab = dataStore.getValue(key = LAST_TAB_KEY, default = 0)
    suspend fun setLastTab(value: Int) {
        dataStore.setValue(LAST_TAB_KEY, value)
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

        private val THEME_KEY = stringPreferencesKey("theme")
        private val LAST_TAB_KEY = intPreferencesKey("last_tab")

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