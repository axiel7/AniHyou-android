package com.axiel7.anihyou.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.App
import com.axiel7.anihyou.BuildConfig
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.PreferencesDataStore.LIST_DISPLAY_MODE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.THEME_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.ui.base.ListMode
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.ListPreference
import com.axiel7.anihyou.ui.composables.PlainPreference
import com.axiel7.anihyou.ui.composables.PreferencesTitle
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ANILIST_ACCOUNT_SETTINGS_URL
import com.axiel7.anihyou.utils.ContextUtils.getActivity
import com.axiel7.anihyou.utils.ContextUtils.openActionView
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

val listModeEntries = mapOf(
    ListMode.STANDARD.name to R.string.standard,
    ListMode.COMPACT.name to R.string.compact,
    ListMode.MINIMAL.name to R.string.minimal,
)

const val SETTINGS_DESTINATION = "settings"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val themePreference = rememberPreference(THEME_PREFERENCE_KEY, THEME_FOLLOW_SYSTEM)
    val listModePreference = rememberPreference(LIST_DISPLAY_MODE_PREFERENCE_KEY, App.listDisplayMode.name)

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.settings),
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            PreferencesTitle(text = stringResource(R.string.display))

            ListPreference(
                title = stringResource(R.string.theme),
                entriesValues = themeEntries,
                preferenceValue = themePreference,
                icon = R.drawable.palette_24,
                onValueChange = { value ->
                    themePreference.value = value
                }
            )

            ListPreference(
                title = stringResource(R.string.list_style),
                entriesValues = listModeEntries,
                preferenceValue = listModePreference,
                icon = R.drawable.format_list_bulleted_24,
                onValueChange = { value ->
                    value?.let { listModePreference.value = ListMode.valueOf(it).name }
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
                        Intent(
                            Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        ).apply {
                            context.startActivity(this)
                        }
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
                navigateBack = {}
            )
        }
    }
}