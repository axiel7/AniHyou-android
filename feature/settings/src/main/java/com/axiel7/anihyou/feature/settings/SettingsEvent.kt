package com.axiel7.anihyou.feature.settings

import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.core.model.AppColorMode
import com.axiel7.anihyou.core.model.DefaultTab
import com.axiel7.anihyou.core.model.ItemsPerRow
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.model.Theme
import com.axiel7.anihyou.core.model.notification.NotificationInterval
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.network.type.UserStaffNameLanguage
import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

interface SettingsEvent {

    fun setTheme(value: Theme)

    fun setUseBlackColors(value: Boolean)

    fun setAppColorMode(value: AppColorMode)

    fun setCustomAppColor(color: Color)

    fun setUseGeneralListStyle(value: Boolean)

    fun setGeneralListStyle(value: ListStyle)

    fun setGridItemsPerRow(value: ItemsPerRow)

    fun setAiringOnMyList(value: Boolean)

    @OptIn(ExperimentalPermissionsApi::class)
    fun setNotificationsEnabled(
        isEnabled: Boolean,
        notificationPermission: PermissionState?,
        createNotificationChannels: () -> Unit,
    )

    fun setNotificationCheckInterval(value: NotificationInterval)

    fun setDisplayAdultContent(value: Boolean)

    fun setTitleLanguage(value: UserTitleLanguage)

    fun setStaffNameLanguage(value: UserStaffNameLanguage)

    fun setScoreFormat(value: ScoreFormat)

    fun setDefaultTab(value: DefaultTab)

    fun setAiringNotification(value: Boolean)

    fun logOut(recreate: () -> Unit)
}