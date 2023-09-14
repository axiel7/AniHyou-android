package com.axiel7.anihyou

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.axiel7.anihyou.data.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.defaultPreferencesDataStore
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
import com.axiel7.anihyou.data.model.notification.NotificationInterval
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.base.ListStyle

class App : Application() {

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        dataStore = defaultPreferencesDataStore
        accessToken = dataStore.getValueSync(ACCESS_TOKEN_PREFERENCE_KEY)
    }

    companion object {
        lateinit var INSTANCE: App
            private set
        val applicationContext: Context get() = INSTANCE.applicationContext

        lateinit var dataStore: DataStore<Preferences>

        var accessToken: String? = null

        var animeListSort = MediaListSort.UPDATED_TIME_DESC.rawValue
        var mangaListSort = MediaListSort.UPDATED_TIME_DESC.rawValue

        var scoreFormat: ScoreFormat = ScoreFormat.POINT_10

        var airingOnMyList = false

        var generalListStyle = ListStyle.STANDARD
        var useGeneralListStyle = true

        var gridItemsPerRow = 0

        var animeCurrentListStyle = ListStyle.STANDARD
        var animePlanningListStyle = ListStyle.STANDARD
        var animeCompletedListStyle = ListStyle.STANDARD
        var animePausedListStyle = ListStyle.STANDARD
        var animeDroppedListStyle = ListStyle.STANDARD
        var animeRepeatingListStyle = ListStyle.STANDARD

        var mangaCurrentListStyle = ListStyle.STANDARD
        var mangaPlanningListStyle = ListStyle.STANDARD
        var mangaCompletedListStyle = ListStyle.STANDARD
        var mangaPausedListStyle = ListStyle.STANDARD
        var mangaDroppedListStyle = ListStyle.STANDARD
        var mangaRepeatingListStyle = ListStyle.STANDARD

        var enabledNotifications = false
        var notificationInterval = NotificationInterval.DAILY
    }
}