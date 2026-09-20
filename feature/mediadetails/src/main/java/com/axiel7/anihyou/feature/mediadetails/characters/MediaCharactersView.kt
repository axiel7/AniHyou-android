package com.axiel7.anihyou.feature.mediadetails.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SelectableDropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.character.localized
import com.axiel7.anihyou.core.model.user.UserMediaListSort
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Route
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.IconButtonWithMenu
import com.axiel7.anihyou.core.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.core.ui.composables.person.PersonItemHorizontalMirrored
import com.axiel7.anihyou.core.ui.composables.person.PersonItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MediaCharactersView(
    arguments: Route.MediaCharacters
) {
    val viewModel: MediaCharactersViewModel = koinViewModel { parametersOf(arguments) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MediaCharactersViewContent(
        uiState = uiState,
        event = viewModel,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MediaCharactersViewContent(
    uiState: MediaCharactersUiState,
    event: MediaCharactersEvent? = null,
) {
    val navActionManager = LocalNavActionManager.current
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.characters),
        navigationIcon = {
            BackIconButton(onClick = navActionManager::goBack)
        },
        actions = {
            LanguageButtonMenu(uiState, event)
        },
        scrollBehavior = topAppBarScrollBehavior,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = 8.dp) + padding
        ) {
            if (uiState.isLoading) {
                items(10) {
                    PersonItemHorizontalPlaceholder()
                }
            }
            items(
                items = uiState.characters,
                contentType = { it }
            ) { item ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PersonItemHorizontal(
                        title = item.node?.name?.userPreferred.orEmpty(),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                        imageUrl = item.node?.image?.medium,
                        imageSize = 60.dp,
                        subtitle = item.role?.localized(),
                        onClick = {
                            item.node?.id?.let(navActionManager::toCharacterDetails)
                        }
                    )

                    item.voiceActors
                        ?.find { it?.commonVoiceActor?.languageV2 == uiState.selectedLanguage }
                        ?.let { voiceActor ->
                            PersonItemHorizontalMirrored(
                                title = voiceActor.commonVoiceActor.name?.userPreferred.orEmpty(),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .weight(1f),
                                imageUrl = voiceActor.commonVoiceActor.image?.medium,
                                imageSize = 60.dp,
                                subtitle = voiceActor.commonVoiceActor.languageV2,
                                onClick = {
                                    voiceActor.id.let(navActionManager::toStaffDetails)
                                }
                            )
                        }
                }
            }
            item(contentType = { 0 }) {
                if (uiState.hasNextPage) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        LoadingIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    LaunchedEffect(uiState.isLoading) {
                        if (!uiState.isLoading) event?.onLoadMore()
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageButtonMenu(
    uiState: MediaCharactersUiState,
    event: MediaCharactersEvent? = null,
) {
    IconButtonWithMenu(
        icon = R.drawable.language_24,
        contentDescription = stringResource(R.string.language),
    ) { onDismiss ->
        uiState.availableLanguages.orEmpty().fastForEachIndexed { index, item ->
            val checked = uiState.selectedLanguage == item
            SelectableDropdownMenuItem(
                selected = checked,
                onClick = {
                    event?.onLanguageSelect(item)
                    onDismiss()
                },
                text = { Text(text = item) },
                shapes = MenuDefaults.itemShape(index, UserMediaListSort.entries.size),
                modifier = Modifier.padding(end = 8.dp),
                selectedLeadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.check_20),
                        contentDescription = null,
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun MediaCharactersPreview() {
    AniHyouTheme {
        MediaCharactersViewContent(
            uiState = MediaCharactersUiState(),
        )
    }
}