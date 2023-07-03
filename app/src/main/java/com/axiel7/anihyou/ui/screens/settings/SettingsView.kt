package com.axiel7.anihyou.ui.screens.settings

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
import com.axiel7.anihyou.data.PreferencesDataStore.THEME_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.USE_GENERAL_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.ui.base.ItemsPerRow
import com.axiel7.anihyou.ui.base.ListStyle
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
import com.axiel7.anihyou.utils.DISCORD_SERVER_URL
import com.axiel7.anihyou.utils.GITHUB_PROFILE_URL
import com.axiel7.anihyou.utils.GITHUB_REPO_URL
import com.axiel7.anihyou.utils.THEME_BLACK
import com.axiel7.anihyou.utils.THEME_DARK
import com.axiel7.anihyou.utils.THEME_FOLLOW_SYSTEM
import com.axiel7.anihyou.utils.THEME_LIGHT
import kotlinx.coroutines.launch

val themeEntries = mapOf(
    THEME_FOLLOW_SYSTEM to R.string.theme_system,
    THEME_LIGHT to R.string.theme_light,
    THEME_DARK to R.string.theme_dark,
    THEME_BLACK to R.string.theme_black
)

val listStyleEntries = ListStyle.values().associate { it.name to it.stringRes }
val itemsPerRowEntries = ItemsPerRow.values().associate { it.value.toString() to it.stringRes }

const val SETTINGS_DESTINATION = "settings"

@OptIn(ExperimentalMaterial3Api::class)
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
    var themePreference by rememberPreference(THEME_PREFERENCE_KEY, THEME_FOLLOW_SYSTEM)
    var useGeneralListStylePreference by rememberPreference(USE_GENERAL_LIST_STYLE_PREFERENCE_KEY, App.useGeneralListStyle)
    var generalListStylePreference by rememberPreference(GENERAL_LIST_STYLE_PREFERENCE_KEY, App.generalListStyle.name)
    var itemsPerRowPreference by rememberPreference(GRID_ITEMS_PER_ROW_PREFERENCE_KEY, App.gridItemsPerRow)
    var airingOnMyList by rememberPreference(AIRING_ON_MY_LIST_PREFERENCE_KEY, App.airingOnMyList)

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

            PreferencesTitle(text = stringResource(R.string.content))
            SwitchPreference(
                title = stringResource(R.string.display_adult_content),
                preferenceValue = viewModel.displayAdultContent,
                icon = R.drawable.no_adult_content_24,
                onValueChange = { value ->
                    if (value != null) {
                        viewModel.onDisplayAdultContentChanged(value)
                        scope.launch { viewModel.updateUser() }
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