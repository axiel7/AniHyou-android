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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.App
import com.axiel7.anihyou.BuildConfig
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.PreferencesDataStore.AIRING_ON_MY_LIST_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.GENERAL_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.GRID_ITEMS_PER_ROW_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.NOTIFICATIONS_ENABLED_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.NOTIFICATION_INTERVAL_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.THEME_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.USE_GENERAL_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.notification.NotificationInterval
import com.axiel7.anihyou.data.model.stringRes
import com.axiel7.anihyou.data.model.user.preferenceValues
import com.axiel7.anihyou.data.model.user.stringRes
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.type.UserStaffNameLanguage
import com.axiel7.anihyou.type.UserTitleLanguage
import com.axiel7.anihyou.ui.base.ItemsPerRow
import com.axiel7.anihyou.ui.base.ListStyle
import com.axiel7.anihyou.ui.base.Theme
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.ListPreference
import com.axiel7.anihyou.ui.composables.PlainPreference
import com.axiel7.anihyou.ui.composables.PreferencesTitle
import com.axiel7.anihyou.ui.composables.SmallCircularProgressIndicator
import com.axiel7.anihyou.ui.composables.SwitchPreference
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
import com.axiel7.anihyou.worker.NotificationWorker
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

val themeEntries = Theme.entries.associate { it.value to it.stringRes }
val listStyleEntries = ListStyle.entries.associate { it.name to it.stringRes }
val itemsPerRowEntries = ItemsPerRow.entries.associate { it.value.toString() to it.stringRes }
val titleLanguageEntries =
    UserTitleLanguage.preferenceValues().associate { it.rawValue to it.stringRes() }
val staffNameLanguageEntries =
    UserStaffNameLanguage.knownValues().associate { it.rawValue to it.stringRes() }
val scoreFormatEntries = ScoreFormat.knownValues().associate { it.rawValue to it.stringRes() }
val notificationIntervalEntries = NotificationInterval.entries.associate { it.name to it.stringRes }

const val SETTINGS_DESTINATION = "settings"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsView(
    navigateToListStyleSettings: () -> Unit,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: SettingsViewModel = viewModel()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    var themePreference by rememberPreference(THEME_PREFERENCE_KEY, Theme.FOLLOW_SYSTEM.value)
    var useGeneralListStylePreference by rememberPreference(
        USE_GENERAL_LIST_STYLE_PREFERENCE_KEY,
        App.useGeneralListStyle
    )
    var generalListStylePreference by rememberPreference(
        GENERAL_LIST_STYLE_PREFERENCE_KEY,
        App.generalListStyle.name
    )
    var itemsPerRowPreference by rememberPreference(
        GRID_ITEMS_PER_ROW_PREFERENCE_KEY,
        App.gridItemsPerRow
    )
    var airingOnMyList by rememberPreference(AIRING_ON_MY_LIST_PREFERENCE_KEY, App.airingOnMyList)
    var scoreFormatPreference by rememberPreference(
        SCORE_FORMAT_PREFERENCE_KEY,
        App.scoreFormat.name
    )
    var notificationsEnabledPreference by rememberPreference(
        NOTIFICATIONS_ENABLED_PREFERENCE_KEY,
        App.enabledNotifications
    )
    var notificationInterval by rememberPreference(
        NOTIFICATION_INTERVAL_PREFERENCE_KEY,
        App.notificationInterval.name
    )

    LaunchedEffect(viewModel) {
        if (!viewModel.isLoading) viewModel.getUserOptions()
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.settings),
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        actions = {
            if (viewModel.isLoading) {
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
                entriesValues = themeEntries,
                preferenceValue = themePreference,
                icon = R.drawable.palette_24,
                onValueChange = { value ->
                    themePreference = value
                }
            )

            SwitchPreference(
                title = stringResource(R.string.use_separated_list_styles),
                preferenceValue = useGeneralListStylePreference?.not(),
                onValueChange = {
                    useGeneralListStylePreference = it?.not()
                    App.useGeneralListStyle = it?.not() ?: true
                }
            )
            if (useGeneralListStylePreference == true) {
                ListPreference(
                    title = stringResource(R.string.list_style),
                    entriesValues = listStyleEntries,
                    preferenceValue = generalListStylePreference,
                    icon = R.drawable.format_list_bulleted_24,
                    onValueChange = { value ->
                        value?.let {
                            val listStyle = ListStyle.valueOf(it)
                            generalListStylePreference = listStyle.name
                            App.generalListStyle = listStyle
                        }
                    }
                )
            } else {
                PlainPreference(
                    title = stringResource(R.string.list_style),
                    icon = R.drawable.format_list_bulleted_24,
                    onClick = navigateToListStyleSettings
                )
            }

            if (generalListStylePreference == ListStyle.GRID.name
                || useGeneralListStylePreference == false
            ) {
                ListPreference(
                    title = stringResource(R.string.items_per_row),
                    entriesValues = itemsPerRowEntries,
                    preferenceValue = itemsPerRowPreference.toString(),
                    icon = R.drawable.grid_view_24,
                    onValueChange = { value ->
                        value?.toIntOrNull()?.let {
                            itemsPerRowPreference = it
                            App.gridItemsPerRow = it
                        }
                    }
                )
            }

            ListPreference(
                title = stringResource(R.string.title_language),
                entriesValues = titleLanguageEntries,
                preferenceValue = viewModel.userOptions?.options?.titleLanguage?.rawValue,
                icon = R.drawable.title_24,
                onValueChange = { value ->
                    value?.let {
                        val titleLanguage = UserTitleLanguage.valueOf(it)
                        viewModel.onTitleLanguageChanged(titleLanguage)
                        context.showToast(R.string.changes_will_take_effect_on_app_restart)
                    }
                }
            )

            ListPreference(
                title = stringResource(R.string.staff_character_name_language),
                entriesValues = staffNameLanguageEntries,
                preferenceValue = viewModel.userOptions?.options?.staffNameLanguage?.rawValue,
                icon = R.drawable.group_24,
                onValueChange = { value ->
                    value?.let {
                        val staffNameLanguage = UserStaffNameLanguage.valueOf(it)
                        viewModel.onStaffNameLanguageChanged(staffNameLanguage)
                        context.showToast(R.string.changes_will_take_effect_on_app_restart)
                    }
                }
            )

            ListPreference(
                title = stringResource(R.string.score_format),
                entriesValues = scoreFormatEntries,
                preferenceValue = scoreFormatPreference,
                icon = R.drawable.star_24,
                onValueChange = { value ->
                    value?.let {
                        scoreFormatPreference = it
                        val scoreFormat = ScoreFormat.valueOf(it)
                        viewModel.onScoreFormatChanged(scoreFormat)
                    }
                }
            )

            PreferencesTitle(text = stringResource(R.string.content))
            SwitchPreference(
                title = stringResource(R.string.display_adult_content),
                preferenceValue = viewModel.userOptions?.options?.displayAdultContent,
                icon = R.drawable.no_adult_content_24,
                onValueChange = { value ->
                    if (value != null) {
                        viewModel.onDisplayAdultContentChanged(value)
                    }
                }
            )
            SwitchPreference(
                title = stringResource(R.string.airing_on_my_list),
                preferenceValue = airingOnMyList,
                subtitle = stringResource(R.string.airing_on_my_list_summary),
                onValueChange = {
                    airingOnMyList = it
                }
            )

            PreferencesTitle(text = stringResource(R.string.notifications))
            SwitchPreference(
                title = stringResource(R.string.push_notifications),
                preferenceValue = notificationsEnabledPreference,
                icon = R.drawable.notifications_24,
                onValueChange = {
                    notificationsEnabledPreference = it
                    if (it == false) NotificationWorker.cancelNotificationWork()
                    else if (it == true) {
                        if (notificationPermission == null || notificationPermission.status.isGranted) {
                            NotificationWorker.scheduleNotificationWork(
                                interval = NotificationInterval.valueOf(
                                    notificationInterval ?: App.notificationInterval.name
                                )
                            )
                        } else {
                            notificationPermission.launchPermissionRequest()
                        }
                    }
                }
            )
            if (notificationsEnabledPreference == true) {
                ListPreference(
                    title = stringResource(R.string.update_interval),
                    entriesValues = notificationIntervalEntries,
                    preferenceValue = notificationInterval,
                    onValueChange = { value ->
                        value?.let {
                            notificationInterval = it
                            NotificationWorker.scheduleNotificationWork(
                                interval = NotificationInterval.valueOf(it)
                            )
                        }
                    }
                )
                SwitchPreference(
                    title = stringResource(R.string.airing_anime_notifications),
                    preferenceValue = viewModel.userOptions?.options?.airingNotifications,
                    icon = R.drawable.podcasts_24,
                    onValueChange = { value ->
                        if (value != null) {
                            viewModel.onAiringNotificationChanged(value)
                        }
                    }
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
                    scope.launch {
                        LoginRepository.removeUserInfo()
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