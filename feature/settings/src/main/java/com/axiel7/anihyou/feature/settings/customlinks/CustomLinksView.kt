package com.axiel7.anihyou.feature.settings.customlinks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.domain.model.CustomLink
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithLargeTopAppBar
import com.axiel7.anihyou.core.ui.composables.PreferencesTitle
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.preferenceShape
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CustomLinksView() {
    val viewModel: CustomLinksViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CustomLinksContent(
        uiState = uiState,
        event = viewModel,
    )
}

@Composable
fun CustomLinksContent(
    uiState: CustomLinksUiState,
    event: CustomLinksEvent?,
) {
    val navActionManager = LocalNavActionManager.current
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    DefaultScaffoldWithLargeTopAppBar(
        title = stringResource(R.string.custom_links),
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            MediaType.knownEntries.forEach { mediaType ->
                var openDialog by remember { mutableStateOf(false) }
                var selectedItem by remember { mutableStateOf<CustomLink?>(null) }

                PreferencesTitle(text = mediaType.localized())

                val customLinks = uiState.customLinks(mediaType)
                customLinks?.forEachIndexed { index, link ->
                    ListItem(
                        link = link,
                        shape = preferenceShape(index, customLinks.size),
                        onClickEdit = {
                            selectedItem = link
                            openDialog = true
                        },
                        onClickDelete = { event?.onLinkRemoved(link) }
                    )
                }

                AddButton(onClick = { openDialog = true })

                if (openDialog) {
                    CustomLinkDialog(
                        mediaType = mediaType,
                        value = selectedItem,
                        onConfirm = { newLink ->
                            event?.onLinkAdded(newLink)
                            selectedItem = null
                            openDialog = false
                        },
                        onDismiss = { openDialog = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun ListItem(
    link: CustomLink,
    shape: Shape,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 1.dp, bottom = 1.dp),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = link.spaceSeparator.toString(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLargeEmphasized,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = link.name,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClickEdit) {
                Icon(
                    painter = painterResource(R.drawable.edit_24),
                    contentDescription = stringResource(R.string.edit)
                )
            }
            IconButton(onClick = onClickDelete) {
                Icon(
                    painter = painterResource(R.drawable.delete_24),
                    contentDescription = stringResource(R.string.delete)
                )
            }
        }
    }
}

@Composable
private fun AddButton(
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp),
        shapes = ButtonDefaults.shapes(),
    ) {
        Icon(
            painter = painterResource(R.drawable.add_24),
            contentDescription = stringResource(R.string.add),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.add))
    }
}

@Preview
@Composable
private fun CustomLinksViewPreview() {
    val sampleLink = CustomLink(
        id = 0,
        name = "nyaa",
        uri = "https://neko.si/?q={name}",
        spaceSeparator = '%',
        mediaType = MediaType.ANIME,
        titleLanguage = null,
    )
    val largeSampleLink = CustomLink(
        id = 0,
        name = "mangadexwithaverylargeurldomain.org",
        uri = "https://mangadexwithaverylargeurldomain.org/search?q={name}",
        spaceSeparator = ' ',
        mediaType = MediaType.MANGA,
        titleLanguage = UserTitleLanguage.NATIVE,
    )
    AniHyouTheme {
        CustomLinksContent(
            uiState = CustomLinksUiState(
                animeLinks = setOf(sampleLink),
                mangaLinks = setOf(sampleLink, largeSampleLink),
            ),
            event = null,
        )
    }
}