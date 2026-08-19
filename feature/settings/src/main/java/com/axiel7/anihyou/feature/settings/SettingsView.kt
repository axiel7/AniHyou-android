package com.axiel7.anihyou.feature.settings

import android.Manifest
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.base.ANILIST_ACCOUNT_SETTINGS_URL
import com.axiel7.anihyou.core.base.DISCORD_SERVER_URL
import com.axiel7.anihyou.core.base.GITHUB_REPO_URL
import com.axiel7.anihyou.core.common.utils.ContextUtils.copyToClipBoard
import com.axiel7.anihyou.core.common.utils.ContextUtils.getActivity
import com.axiel7.anihyou.core.common.utils.ContextUtils.openActionView
import com.axiel7.anihyou.core.common.utils.ContextUtils.openByDefaultSettings
import com.axiel7.anihyou.core.common.utils.ContextUtils.openLink
import com.axiel7.anihyou.core.model.AppColorMode
import com.axiel7.anihyou.core.model.DefaultTab
import com.axiel7.anihyou.core.model.ItemsPerRow
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.model.Theme
import com.axiel7.anihyou.core.model.TranslatorApp
import com.axiel7.anihyou.core.model.entriesLocalized
import com.axiel7.anihyou.core.model.notification.NotificationInterval
import com.axiel7.anihyou.core.model.user.entriesLocalized
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.network.type.UserStaffNameLanguage
import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalIsLanguageEn
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.ListPreference
import com.axiel7.anihyou.core.ui.composables.PlainPreference
import com.axiel7.anihyou.core.ui.composables.PreferencesTitle
import com.axiel7.anihyou.core.ui.composables.ScoreStepsPreferenceSheet
import com.axiel7.anihyou.core.ui.composables.SwitchPreference
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.SmallCircularProgressIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.settings.composables.CustomColorPreference
import com.axiel7.anihyou.feature.settings.composables.LanguagePreference
import com.axiel7.anihyou.feature.worker.NotificationWorker.Companion.createDefaultNotificationChannels
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.materialkolor.PaletteStyle
import org.koin.compose.viewmodel.koinViewModel

private const val versionString = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsView() {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    SettingsContent(
        uiState = uiState,
        event = viewModel,
        notificationPermission = notificationPermission,
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    event: SettingsEvent?,
    notificationPermission: PermissionState?,
) {
    val navActionManager = LocalNavActionManager.current
    val isEnglishLocale = LocalIsLanguageEn.current
    val context = LocalContext.current
    val snackbarManager = rememberSnackbarManager()
    val isDarkTheme = (uiState.theme == Theme.FOLLOW_SYSTEM && isSystemInDarkTheme())
            || uiState.theme == Theme.DARK

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    var showConfirmDialog by remember { mutableStateOf(false) }

    val topShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
    val bottomShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
    val middleShape = RoundedCornerShape(4.dp)
    val singleShape = RoundedCornerShape(24.dp)

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    LaunchedEffect(isDarkTheme) {
        if (!isDarkTheme && uiState.useBlackColors) {
            event?.setUseBlackColors(false)
        }
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.settings),
        snackbarHost = snackbarManager::SnackbarHost,
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
            PreferencesTitle(text = stringResource(R.string.general))

            LanguagePreference(shape = topShape)

            if (!isEnglishLocale) {
                ListPreference(
                    title = stringResource(R.string.translator_app),
                    entriesValues = TranslatorApp.entriesLocalized,
                    preferenceValue = uiState.translatorApp,
                    icon = R.drawable.translate_24,
                    onValueChange = { value ->
                        event?.setTranslatorApp(value)
                    },
                    shape = middleShape
                )
            }

            ListPreference(
                title = stringResource(R.string.default_tab),
                entriesValues = DefaultTab.entriesLocalized,
                preferenceValue = uiState.defaultTab,
                icon = R.drawable.home_24,
                onValueChange = { event?.setDefaultTab(it) },
                shape = bottomShape
            )


            PreferencesTitle(text = stringResource(R.string.display))

            ListPreference(
                title = stringResource(R.string.theme),
                entriesValues = Theme.entriesLocalized,
                preferenceValue = uiState.theme,
                icon = R.drawable.palette_24,
                onValueChange = { event?.setTheme(it) },
                shape = topShape
            )

            if (isDarkTheme) {
                SwitchPreference(
                    title = stringResource(R.string.black_theme_variant),
                    preferenceValue = uiState.useBlackColors,
                    onValueChange = { event?.setUseBlackColors(it) },
                    shape = middleShape
                )
            }

            ListPreference(
                title = stringResource(R.string.color),
                entriesValues = AppColorMode.entriesLocalized,
                preferenceValue = uiState.appColorMode,
                icon = R.drawable.colors_24,
                onValueChange = { event?.setAppColorMode(it) },
                shape = middleShape
            )
            if (uiState.appColorMode == AppColorMode.CUSTOM) {
                CustomColorPreference(
                    color = uiState.appColor,
                    onColorChanged = { event?.setCustomAppColor(it) },
                    shape = middleShape
                )
            }

            ListPreference(
                title = stringResource(R.string.color_palette),
                values = PaletteStyle.entries.map { it.name },
                preferenceValue = uiState.colorPaletteStyle,
                icon = R.drawable.format_paint_24,
                onValueChange = { event?.setColorPalette(it) },
                shape = bottomShape
            )


            PreferencesTitle(text = stringResource(R.string.content))

            if (uiState.isLoggedIn) {
                ListPreference(
                    title = stringResource(R.string.title_language),
                    entriesValues = UserTitleLanguage.entriesLocalized,
                    preferenceValue = uiState.userSettings?.options?.titleLanguage,
                    icon = R.drawable.title_24,
                    onValueChange = { value ->
                        event?.setTitleLanguage(value)
                        snackbarManager.showMessage(R.string.changes_will_take_effect_on_app_restart)
                    },
                    shape = topShape
                )

                ListPreference(
                    title = stringResource(R.string.score_format),
                    entriesValues = ScoreFormat.entriesLocalized,
                    preferenceValue = uiState.scoreFormat,
                    icon = R.drawable.star_24,
                    onValueChange = { event?.setScoreFormat(it) },
                    shape = middleShape
                )

                if (uiState.scoreFormat == ScoreFormat.POINT_10_DECIMAL ||
                    uiState.scoreFormat == ScoreFormat.POINT_10 ||
                    uiState.scoreFormat == ScoreFormat.POINT_100
                ) {
                    ScoreStepsPreferenceSheet(
                        title = stringResource(R.string.score_steps),
                        icon = R.drawable.star_24,
                        changeValue = { event?.setScoreStep(it) },
                        scoreFormat = uiState.scoreFormat,
                        initialValue = uiState.scoreStep,
                        shape = middleShape
                    )
                }

                ListPreference(
                    title = stringResource(R.string.staff_character_name_language),
                    entriesValues = UserStaffNameLanguage.entriesLocalized,
                    preferenceValue = uiState.userSettings?.options?.staffNameLanguage,
                    icon = R.drawable.group_24,
                    onValueChange = { value ->
                        event?.setStaffNameLanguage(value)
                        snackbarManager.showMessage(R.string.changes_will_take_effect_on_app_restart)
                    },
                    shape = middleShape
                )

                SwitchPreference(
                    title = stringResource(R.string.hide_scores),
                    preferenceValue = uiState.hideScores,
                    icon = R.drawable.star_half_24,
                    onValueChange = { event?.setHideScores(it) },
                    shape = middleShape
                )
            }


            SwitchPreference(
                title = stringResource(R.string.display_adult_content),
                preferenceValue = uiState.userSettings?.options?.displayAdultContent,
                icon = R.drawable.no_adult_content_24,
                onValueChange = { event?.setDisplayAdultContent(it) },
                shape = if (uiState.isLoggedIn) middleShape else topShape
            )

            SwitchPreference(
                title = stringResource(R.string.blur_adult_content),
                preferenceValue = uiState.blurAdultContent,
                icon = R.drawable.blur_on_24,
                onValueChange = { event?.setBlurAdultContent(it) },
                shape = bottomShape
            )


            if (uiState.isLoggedIn) {
                PreferencesTitle(text = stringResource(R.string.list))

                SwitchPreference(
                    title = stringResource(R.string.use_separated_list_styles),
                    preferenceValue = uiState.useGeneralListStyle?.not(),
                    onValueChange = {
                        event?.setUseGeneralListStyle(it.not())
                    },
                    shape = topShape
                )

                if (uiState.useGeneralListStyle == true) {
                    ListPreference(
                        title = stringResource(R.string.list_style),
                        entriesValues = ListStyle.entriesLocalized,
                        preferenceValue = uiState.generalListStyle,
                        icon = R.drawable.format_list_bulleted_24,
                        onValueChange = { event?.setGeneralListStyle(it) },
                        shape = middleShape
                    )
                } else {
                    PlainPreference(
                        title = stringResource(R.string.list_style),
                        icon = R.drawable.format_list_bulleted_24,
                        onClick = navActionManager::toListStyleSettings,
                        shape = middleShape
                    )
                }

                if (uiState.generalListStyle == ListStyle.GRID || uiState.useGeneralListStyle == false) {
                    ListPreference(
                        title = stringResource(R.string.items_per_row),
                        entriesValues = ItemsPerRow.entriesLocalized,
                        preferenceValue = uiState.gridItemsPerRow,
                        icon = R.drawable.grid_view_24,
                        onValueChange = { event?.setGridItemsPerRow(it) },
                        shape = middleShape
                    )
                }

                PlainPreference(
                    title = stringResource(R.string.custom_lists),
                    icon = R.drawable.playlist_add_24,
                    onClick = navActionManager::toCustomLists,
                    shape = middleShape
                )

                SwitchPreference(
                    title = stringResource(R.string.show_low_priority),
                    preferenceValue = uiState.showLowPriority,
                    icon = R.drawable.counter_0_24,
                    onValueChange = { event?.setShowLowPriority(it) },
                    shape = middleShape
                )

                PlainPreference(
                    title = stringResource(R.string.priority_color_change),
                    icon = R.drawable.colors_24,
                    onClick = navActionManager::toPriorityColors,
                    shape = middleShape
                )

                SwitchPreference(
                    title = stringResource(R.string.airing_on_my_list),
                    preferenceValue = uiState.airingOnMyList,
                    subtitle = stringResource(R.string.airing_on_my_list_summary),
                    onValueChange = { event?.setAiringOnMyList(it) },
                    shape = bottomShape
                )
            }


            if (uiState.isLoggedIn) {
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
                    },
                    shape = if (uiState.isNotificationsEnabled == true) topShape else singleShape
                )

                if (uiState.isNotificationsEnabled == true) {
                    ListPreference(
                        title = stringResource(R.string.update_interval),
                        entriesValues = NotificationInterval.entriesLocalized,
                        preferenceValue = uiState.notificationCheckInterval,
                        onValueChange = { event?.setNotificationCheckInterval(it) },
                        shape = middleShape
                    )

                    SwitchPreference(
                        title = stringResource(R.string.airing_anime_notifications),
                        preferenceValue = uiState.userSettings?.options?.airingNotifications,
                        icon = R.drawable.podcasts_24,
                        onValueChange = { event?.setAiringNotification(it) },
                        shape = bottomShape
                    )
                }


                PreferencesTitle(text = stringResource(R.string.account))

                PlainPreference(
                    title = stringResource(R.string.anilist_account_settings),
                    icon = R.drawable.manage_accounts_24,
                    onClick = {
                        context.openLink(ANILIST_ACCOUNT_SETTINGS_URL)
                    },
                    shape = topShape
                )
                PlainPreference(
                    title = stringResource(R.string.logout),
                    icon = R.drawable.logout_24,
                    onClick = { showConfirmDialog = true },
                    shape = bottomShape
                )
            }


            PreferencesTitle(text = stringResource(R.string.information))

            PlainPreference(
                title = stringResource(R.string.github_repository),
                icon = R.drawable.github_24,
                onClick = {
                    context.openActionView(GITHUB_REPO_URL)
                },
                shape = topShape
            )

            PlainPreference(
                title = "Discord",
                icon = R.drawable.discord_24,
                onClick = {
                    context.openActionView(DISCORD_SERVER_URL)
                },
                shape = middleShape
            )

            PlainPreference(
                title = stringResource(R.string.version),
                subtitle = versionString,
                icon = R.drawable.anihyou_24,
                onClick = {
                    context.copyToClipBoard(versionString)
                },
                shape = middleShape
            )

            PlainPreference(
                title = stringResource(R.string.contributors),
                icon = R.drawable.code_24,
                onClick = navActionManager::toContributors,
                shape = middleShape
            )

            PlainPreference(
                title = stringResource(R.string.translations),
                icon = R.drawable.language_24,
                onClick = navActionManager::toTranslations,
                shape = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) middleShape else bottomShape
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PlainPreference(
                    title = stringResource(R.string.open_al_links_in_the_app),
                    icon = R.drawable.open_in_new_24,
                    onClick = {
                        context.openByDefaultSettings()
                    },
                    shape = bottomShape
                )
            }
        }//: Column
    }//: Scaffold

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(stringResource(R.string.logout)) },
            text = { Text(stringResource(R.string.logout_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        event?.logOut {
                            context.getActivity()?.recreate()
                        }
                        showConfirmDialog = false
                    }
                ) {
                    Text(stringResource(R.string.logout))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
private fun SettingsViewPreview() {
    AniHyouTheme {
        Surface {
            SettingsContent(
                uiState = SettingsUiState(isLoggedIn = true),
                event = null,
                notificationPermission = null,
            )
        }
    }
}