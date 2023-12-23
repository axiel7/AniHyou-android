package com.axiel7.anihyou.ui.screens.settings.liststyle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.ListPreference
import com.axiel7.anihyou.ui.composables.PreferencesTitle
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun ListStyleSettingsView(
    navActionManager: NavActionManager,
) {
    val viewModel: ListStyleSettingsViewModel = hiltViewModel()

    ListStyleSettingsContent(
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListStyleSettingsContent(
    event: ListStyleSettingsEvent?,
    navActionManager: NavActionManager,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.list_style),
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            Text(
                text = stringResource(R.string.changes_will_take_effect_on_app_restart),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            PreferencesTitle(text = stringResource(R.string.anime_list))
            MediaListStatus.entries.forEach { status ->
                val preference = event?.getAnimeListStyle(status)?.collectAsStateWithLifecycle()

                ListPreference(
                    title = status.localized(mediaType = MediaType.ANIME),
                    entriesValues = ListStyle.entriesLocalized,
                    preferenceValue = preference?.value,
                    icon = status.icon(),
                    onValueChange = { value ->
                        event?.setAnimeListStyle(status, value)
                    }
                )
            }

            PreferencesTitle(text = stringResource(R.string.manga_list))
            MediaListStatus.entries.forEach { status ->
                val preference = event?.getMangaListStyle(status)?.collectAsStateWithLifecycle()

                ListPreference(
                    title = status.localized(mediaType = MediaType.MANGA),
                    entriesValues = ListStyle.entriesLocalized,
                    preferenceValue = preference?.value,
                    icon = status.icon(),
                    onValueChange = { value ->
                        event?.setMangaListStyle(status, value)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ListStyleSettingsViewPreview() {
    AniHyouTheme {
        Surface {
            ListStyleSettingsContent(
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}