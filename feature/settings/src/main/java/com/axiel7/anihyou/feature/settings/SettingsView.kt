package com.axiel7.anihyou.feature.settings

import android.Manifest
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.common.ANILIST_ACCOUNT_SETTINGS_URL
import com.axiel7.anihyou.core.common.DISCORD_SERVER_URL
import com.axiel7.anihyou.core.common.GITHUB_PROFILE_URL
import com.axiel7.anihyou.core.common.GITHUB_REPO_URL
import com.axiel7.anihyou.core.model.AppColorMode
import com.axiel7.anihyou.core.model.DefaultTab
import com.axiel7.anihyou.core.model.ItemsPerRow
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.model.Theme
import com.axiel7.anihyou.core.model.entriesLocalized
import com.axiel7.anihyou.core.model.notification.NotificationInterval
import com.axiel7.anihyou.core.model.user.entriesLocalized
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.network.type.UserStaffNameLanguage
import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.ListPreference
import com.axiel7.anihyou.core.ui.composables.PlainPreference
import com.axiel7.anihyou.core.ui.composables.PreferencesTitle
import com.axiel7.anihyou.core.ui.composables.SwitchPreference
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.SmallCircularProgressIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.ContextUtils.copyToClipBoard
import com.axiel7.anihyou.core.ui.utils.ContextUtils.getActivity
import com.axiel7.anihyou.core.ui.utils.ContextUtils.openActionView
import com.axiel7.anihyou.core.ui.utils.ContextUtils.openByDefaultSettings
import com.axiel7.anihyou.core.ui.utils.ContextUtils.openLink
import com.axiel7.anihyou.core.ui.utils.ContextUtils.showToast
import com.axiel7.anihyou.feature.settings.BuildConfig
import com.axiel7.anihyou.feature.settings.composables.CustomColorPreference
import com.axiel7.anihyou.feature.settings.composables.LanguagePreference
import com.axiel7.anihyou.feature.worker.NotificationWorker.Companion.createDefaultNotificationChannels
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel

private const val versionString = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsView(
    navActionManager: NavActionManager
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    SettingsContent(
        uiState = uiState,
        event = viewModel,
        notificationPermission = notificationPermission,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    event: SettingsEvent?,
    notificationPermission: PermissionState?,
    navActionManager: NavActionManager,
) {
    val context = LocalContext.current
    val isDarkTheme = (uiState.theme == Theme.FOLLOW_SYSTEM && isSystemInDarkTheme())
            || uiState.theme == Theme.DARK

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    LaunchedEffect(isDarkTheme) {
        if (!isDarkTheme && uiState.useBlackColors) {
            event?.setUseBlackColors(false)
        }
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.settings),
        navigationIcon = {
            BackIconButton(onClick = navActionManager::goBack)
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
                onValueChange = { event?.setTheme(it) }
            )

            if (isDarkTheme) {
                SwitchPreference(
                    title = stringResource(R.string.black_theme_variant),
                    preferenceValue = uiState.useBlackColors,
                    onValueChange = { event?.setUseBlackColors(it) }
                )
            }

            ListPreference(
                title = stringResource(R.string.color),
                entriesValues = AppColorMode.entriesLocalized,
                preferenceValue = uiState.appColorMode,
                icon = R.drawable.colors_24,
                onValueChange = { event?.setAppColorMode(it) }
            )
            if (uiState.appColorMode == AppColorMode.CUSTOM) {
                CustomColorPreference(
                    color = uiState.appColor,
                    onColorChanged = { event?.setCustomAppColor(it) }
                )
            }

            LanguagePreference()

            if (uiState.isLoggedIn) {
                ListPreference(
                    title = stringResource(R.string.title_language),
                    entriesValues = UserTitleLanguage.entriesLocalized,
                    preferenceValue = uiState.userSettings?.options?.titleLanguage,
                    icon = R.drawable.title_24,
                    onValueChange = { value ->
                        event?.setTitleLanguage(value)
                        context.showToast(R.string.changes_will_take_effect_on_app_restart)
                    }
                )

                ListPreference(
                    title = stringResource(R.string.staff_character_name_language),
                    entriesValues = UserStaffNameLanguage.entriesLocalized,
                    preferenceValue = uiState.userSettings?.options?.staffNameLanguage,
                    icon = R.drawable.group_24,
                    onValueChange = { value ->
                        event?.setStaffNameLanguage(value)
                        context.showToast(R.string.changes_will_take_effect_on_app_restart)
                    }
                )

                ListPreference(
                    title = stringResource(R.string.score_format),
                    entriesValues = ScoreFormat.entriesLocalized,
                    preferenceValue = uiState.scoreFormat,
                    icon = R.drawable.star_24,
                    onValueChange = { event?.setScoreFormat(it) }
                )

                ListPreference(
                    title = stringResource(R.string.default_tab),
                    entriesValues = DefaultTab.entriesLocalized,
                    preferenceValue = uiState.defaultTab,
                    icon = R.drawable.home_24,
                    onValueChange = { event?.setDefaultTab(it) }
                )

                PreferencesTitle(text = stringResource(R.string.list))

                SwitchPreference(
                    title = stringResource(R.string.use_separated_list_styles),
                    preferenceValue = uiState.useGeneralListStyle?.not(),
                    onValueChange = {
                        event?.setUseGeneralListStyle(it.not())
                    }
                )
                if (uiState.useGeneralListStyle == true) {
                    ListPreference(
                        title = stringResource(R.string.list_style),
                        entriesValues = ListStyle.entriesLocalized,
                        preferenceValue = uiState.generalListStyle,
                        icon = R.drawable.format_list_bulleted_24,
                        onValueChange = { event?.setGeneralListStyle(it) }
                    )
                } else {
                    PlainPreference(
                        title = stringResource(R.string.list_style),
                        icon = R.drawable.format_list_bulleted_24,
                        onClick = navActionManager::toListStyleSettings
                    )
                }

                if (uiState.generalListStyle == ListStyle.GRID || uiState.useGeneralListStyle == false) {
                    ListPreference(
                        title = stringResource(R.string.items_per_row),
                        entriesValues = ItemsPerRow.entriesLocalized,
                        preferenceValue = uiState.gridItemsPerRow,
                        icon = R.drawable.grid_view_24,
                        onValueChange = { event?.setGridItemsPerRow(it) }
                    )
                }
                PlainPreference(
                    title = stringResource(R.string.custom_lists),
                    icon = R.drawable.playlist_add_24,
                    onClick = navActionManager::toCustomLists
                )

                PreferencesTitle(text = stringResource(R.string.content))

                SwitchPreference(
                    title = stringResource(R.string.display_adult_content),
                    preferenceValue = uiState.userSettings?.options?.displayAdultContent,
                    icon = R.drawable.no_adult_content_24,
                    onValueChange = { event?.setDisplayAdultContent(it) }
                )
                SwitchPreference(
                    title = stringResource(R.string.airing_on_my_list),
                    preferenceValue = uiState.airingOnMyList,
                    subtitle = stringResource(R.string.airing_on_my_list_summary),
                    onValueChange = { event?.setAiringOnMyList(it) }
                )

                PreferencesTitle(text = stringResource(R.string.notifications))

                SwitchPreference(
                    title = stringResource(R.string.push_notifications),
                    preferenceValue = uiState.isNotificationsEnabled,
                    icon = R.drawable.notifications_24,
                    onValueChange = { isEnabled ->
                        event?.setNotificationsEnabled(
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
                        preferenceValue = uiState.notificationCheckInterval,
                        onValueChange = { event?.setNotificationCheckInterval(it) }
                    )
                    SwitchPreference(
                        title = stringResource(R.string.airing_anime_notifications),
                        preferenceValue = uiState.userSettings?.options?.airingNotifications,
                        icon = R.drawable.podcasts_24,
                        onValueChange = { event?.setAiringNotification(it) }
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
                        event?.logOut {
                            context.getActivity()?.recreate()
                        }
                    }
                )
            }

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
                subtitle = versionString,
                icon = R.drawable.anihyou_24,
                onClick = {
                    context.copyToClipBoard(versionString)
                }
            )

            PlainPreference(
                title = stringResource(R.string.developed_by_axiel7),
                icon = R.drawable.code_24,
                onClick = {
                    context.openActionView(GITHUB_PROFILE_URL)
                }
            )

            PlainPreference(
                title = stringResource(R.string.translations),
                icon = R.drawable.language_24,
                onClick = navActionManager::toTranslations
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

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun SettingsViewPreview() {
    AniHyouTheme {
        Surface {
            SettingsContent(
                uiState = SettingsUiState(isLoggedIn = true),
                event = null,
                notificationPermission = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}