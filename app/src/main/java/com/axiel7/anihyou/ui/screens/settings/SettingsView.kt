package com.axiel7.anihyou.ui.screens.settings

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.BuildConfig
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.entriesLocalized
import com.axiel7.anihyou.data.model.notification.NotificationInterval
import com.axiel7.anihyou.data.model.user.entriesLocalized
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.type.UserStaffNameLanguage
import com.axiel7.anihyou.type.UserTitleLanguage
import com.axiel7.anihyou.ui.common.AppColorMode
import com.axiel7.anihyou.ui.common.ItemsPerRow
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.Theme
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.ListPreference
import com.axiel7.anihyou.ui.composables.PlainPreference
import com.axiel7.anihyou.ui.composables.PreferencesTitle
import com.axiel7.anihyou.ui.composables.SmallCircularProgressIndicator
import com.axiel7.anihyou.ui.composables.SwitchPreference
import com.axiel7.anihyou.ui.screens.home.HomeTab
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ANILIST_ACCOUNT_SETTINGS_URL
import com.axiel7.anihyou.utils.ContextUtils.getActivity
import com.axiel7.anihyou.utils.ContextUtils.openActionView
import com.axiel7.anihyou.utils.ContextUtils.openByDefaultSettings
import com.axiel7.anihyou.utils.ContextUtils.openLink
import com.axiel7.anihyou.utils.ContextUtils.showToast
import com.axiel7.anihyou.utils.DISCORD_SERVER_URL
import com.axiel7.anihyou.utils.GITHUB_PROFILE_URL
import com.axiel7.anihyou.utils.GITHUB_REPO_URL
import com.axiel7.anihyou.worker.NotificationWorker.Companion.createDefaultNotificationChannels
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

const val SETTINGS_DESTINATION = "settings"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsView(
    navigateToListStyleSettings: () -> Unit,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val notificationCheckInterval by viewModel.notificationCheckInterval.collectAsStateWithLifecycle(
        initialValue = NotificationInterval.DAILY
    )

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.settings),
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        actions = {
            if (uiState.isLoading) {
                SmallCircularProgressIndicator(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            PreferencesTitle(text = stringResource(R.string.display))

            ListPreference(
                title = stringResource(R.string.theme),
                entriesValues = Theme.entriesLocalized,
                preferenceValue = uiState.theme,
                icon = R.drawable.palette_24,
                onValueChange = viewModel::setTheme
            )

            ListPreference(
                title = stringResource(R.string.color),
                entriesValues = AppColorMode.entriesLocalized,
                preferenceValue = uiState.appColorMode,
                icon = R.drawable.colors_24,
                onValueChange = viewModel::setAppColorMode
            )

            ListPreference(
                title = stringResource(R.string.default_home_tab),
                entriesValues = HomeTab.entriesLocalized,
                preferenceValue = uiState.defaultHomeTab,
                icon = R.drawable.home_24,
                onValueChange = viewModel::setDefaultHomeTab
            )

            SwitchPreference(
                title = stringResource(R.string.use_separated_list_styles),
                preferenceValue = uiState.useGeneralListStyle?.not(),
                onValueChange = {
                    viewModel.setUseGeneralListStyle(it.not())
                }
            )
            if (uiState.useGeneralListStyle == true) {
                ListPreference(
                    title = stringResource(R.string.list_style),
                    entriesValues = ListStyle.entriesLocalized,
                    preferenceValue = uiState.generalListStyle,
                    icon = R.drawable.format_list_bulleted_24,
                    onValueChange = viewModel::setGeneralListStyle
                )
            } else {
                PlainPreference(
                    title = stringResource(R.string.list_style),
                    icon = R.drawable.format_list_bulleted_24,
                    onClick = navigateToListStyleSettings
                )
            }

            if (uiState.generalListStyle == ListStyle.GRID || uiState.useGeneralListStyle == false) {
                ListPreference(
                    title = stringResource(R.string.items_per_row),
                    entriesValues = ItemsPerRow.entriesLocalized,
                    preferenceValue = uiState.gridItemsPerRow,
                    icon = R.drawable.grid_view_24,
                    onValueChange = viewModel::setGridItemsPerRow
                )
            }

            ListPreference(
                title = stringResource(R.string.title_language),
                entriesValues = UserTitleLanguage.entriesLocalized,
                preferenceValue = uiState.userOptions?.options?.titleLanguage,
                icon = R.drawable.title_24,
                onValueChange = { value ->
                    viewModel.setTitleLanguage(value)
                    context.showToast(R.string.changes_will_take_effect_on_app_restart)
                }
            )

            ListPreference(
                title = stringResource(R.string.staff_character_name_language),
                entriesValues = UserStaffNameLanguage.entriesLocalized,
                preferenceValue = uiState.userOptions?.options?.staffNameLanguage,
                icon = R.drawable.group_24,
                onValueChange = { value ->
                    viewModel.setStaffNameLanguage(value)
                    context.showToast(R.string.changes_will_take_effect_on_app_restart)
                }
            )

            ListPreference(
                title = stringResource(R.string.score_format),
                entriesValues = ScoreFormat.entriesLocalized,
                preferenceValue = uiState.scoreFormat,
                icon = R.drawable.star_24,
                onValueChange = viewModel::setScoreFormat
            )

            PreferencesTitle(text = stringResource(R.string.content))
            SwitchPreference(
                title = stringResource(R.string.display_adult_content),
                preferenceValue = uiState.userOptions?.options?.displayAdultContent,
                icon = R.drawable.no_adult_content_24,
                onValueChange = viewModel::setDisplayAdultContent
            )
            SwitchPreference(
                title = stringResource(R.string.airing_on_my_list),
                preferenceValue = uiState.airingOnMyList,
                subtitle = stringResource(R.string.airing_on_my_list_summary),
                onValueChange = viewModel::setAiringOnMyList
            )

            PreferencesTitle(text = stringResource(R.string.notifications))
            SwitchPreference(
                title = stringResource(R.string.push_notifications),
                preferenceValue = uiState.isNotificationsEnabled,
                icon = R.drawable.notifications_24,
                onValueChange = { isEnabled ->
                    viewModel.setNotificationsEnabled(
                        isEnabled = isEnabled,
                        notificationPermission = notificationPermission,
                        createNotificationChannels = {
                            context.createDefaultNotificationChannels()
                        }
                    )
                }
            )
            if (uiState.isNotificationsEnabled == true) {
                ListPreference(
                    title = stringResource(R.string.update_interval),
                    entriesValues = NotificationInterval.entriesLocalized,
                    preferenceValue = notificationCheckInterval,
                    onValueChange = viewModel::setNotificationCheckInterval
                )
                SwitchPreference(
                    title = stringResource(R.string.airing_anime_notifications),
                    preferenceValue = uiState.userOptions?.options?.airingNotifications,
                    icon = R.drawable.podcasts_24,
                    onValueChange = viewModel::setAiringNotification
                )
            }

            PreferencesTitle(text = stringResource(R.string.account))
            PlainPreference(
                title = stringResource(R.string.anilist_account_settings),
                icon = R.drawable.manage_accounts_24,
                onClick = {
                    context.openLink(ANILIST_ACCOUNT_SETTINGS_URL)
                }
            )
            PlainPreference(
                title = stringResource(R.string.logout),
                icon = R.drawable.logout_24,
                onClick = {
                    viewModel.logOut {
                        context.getActivity()?.recreate()
                    }
                }
            )

            PreferencesTitle(text = stringResource(R.string.information))
            PlainPreference(
                title = stringResource(R.string.github_repository),
                icon = R.drawable.github_24,
                onClick = {
                    context.openActionView(GITHUB_REPO_URL)
                }
            )

            PlainPreference(
                title = "Discord",
                icon = R.drawable.discord_24,
                onClick = {
                    context.openActionView(DISCORD_SERVER_URL)
                }
            )

            PlainPreference(
                title = stringResource(R.string.version),
                subtitle = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                icon = R.drawable.anihyou_24,
                onClick = {

                }
            )

            PlainPreference(
                title = stringResource(R.string.developed_by_axiel7),
                icon = R.drawable.code_24,
                onClick = {
                    context.openActionView(GITHUB_PROFILE_URL)
                }
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PlainPreference(
                    title = stringResource(R.string.open_al_links_in_the_app),
                    icon = R.drawable.open_in_new_24,
                    onClick = {
                        context.openByDefaultSettings()
                    }
                )
            }
        }//: Column
    }//: Scaffold
}

@Preview
@Composable
fun SettingsViewPreview() {
    AniHyouTheme {
        Surface {
            SettingsView(
                navigateToListStyleSettings = {},
                navigateBack = {}
            )
        }
    }
}