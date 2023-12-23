package com.axiel7.anihyou.ui.screens.settings

import com.axiel7.anihyou.data.model.notification.NotificationInterval
import com.axiel7.anihyou.fragment.UserOptionsFragment
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.AppColorMode
import com.axiel7.anihyou.ui.common.ItemsPerRow
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.Theme
import com.axiel7.anihyou.ui.common.state.UiState
import com.axiel7.anihyou.ui.screens.home.HomeTab

data class SettingsUiState(
    override val error: String? = null,
    override val isLoading: Boolean = true,
    val theme: Theme? = null,
    val appColorMode: AppColorMode? = null,
    val useGeneralListStyle: Boolean? = null,
    val generalListStyle: ListStyle? = null,
    val gridItemsPerRow: ItemsPerRow? = null,
    val defaultHomeTab: HomeTab? = null,
    val airingOnMyList: Boolean? = null,
    val scoreFormat: ScoreFormat? = null,
    val isNotificationsEnabled: Boolean? = null,
    val notificationCheckInterval: NotificationInterval = NotificationInterval.DAILY,
    val userOptions: UserOptionsFragment? = null,
) : UiState<SettingsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
