package com.axiel7.anihyou.ui.screens.settings

import com.axiel7.anihyou.data.model.notification.NotificationInterval
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.type.UserStaffNameLanguage
import com.axiel7.anihyou.type.UserTitleLanguage
import com.axiel7.anihyou.ui.common.AppColorMode
import com.axiel7.anihyou.ui.common.ItemsPerRow
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.Theme
import com.axiel7.anihyou.ui.screens.home.HomeTab
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

interface SettingsEvent {

    fun setTheme(value: Theme)

    fun setAppColorMode(value: AppColorMode)

    fun setUseGeneralListStyle(value: Boolean)

    fun setGeneralListStyle(value: ListStyle)

    fun setGridItemsPerRow(value: ItemsPerRow)

    fun setDefaultHomeTab(value: HomeTab)

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

    fun setAiringNotification(value: Boolean)

    fun logOut(recreate: () -> Unit)
}