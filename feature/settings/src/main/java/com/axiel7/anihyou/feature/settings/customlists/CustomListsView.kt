package com.axiel7.anihyou.feature.settings.customlists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.PlainPreference
import com.axiel7.anihyou.core.ui.composables.PreferencesTitle
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.DialogWithTextInput
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.SmallCircularProgressIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun CustomListsView(
    navActionManager: NavActionManager,
) {
    val viewModel: CustomListsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CustomListsContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomListsContent(
    uiState: CustomListsUiState,
    event: CustomListsEvent?,
    navActionManager: NavActionManager,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.custom_lists),
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        actions = {
            if (uiState.isLoading) {
                SmallCircularProgressIndicator()
            } else {
                IconButton(onClick = { event?.updateCustomLists() }) {
                    Icon(
                        painter = painterResource(R.drawable.save_24),
                        contentDescription = stringResource(R.string.save)
                    )
                }
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
            MediaType.knownEntries.forEach { mediaType ->
                var openDialog by remember { mutableStateOf(false) }
                PreferencesTitle(text = mediaType.localized())

                uiState.customLists(mediaType)?.forEach { list ->
                    ListItem(
                        list = list,
                        onClickDelete = { event?.onListRemoved(list, mediaType) }
                    )
                }

                PlainPreference(
                    title = stringResource(R.string.add),
                    icon = R.drawable.add_24,
                    onClick = { openDialog = true }
                )

                if (openDialog) {
                    var newList by remember { mutableStateOf("") }
                    DialogWithTextInput(
                        title = mediaType.localized(),
                        label = stringResource(R.string.list_name),
                        value = newList,
                        onValueChange = { newList = it },
                        onConfirm = {
                            event?.onListAdded(newList, mediaType)
                            openDialog = false
                            newList = ""
                        },
                        onDismiss = { openDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun ListItem(
    list: String,
    onClickDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .padding(16.dp)
                .size(24.dp)
        )
        Text(
            text = list,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        )
        IconButton(onClick = onClickDelete) {
            Icon(
                painter = painterResource(R.drawable.delete_24),
                contentDescription = stringResource(R.string.delete)
            )
        }
    }
}

@Preview
@Composable
private fun CustomListsViewPreview() {
    val animeLists = remember {
        mutableStateListOf("AOTY", "Best SoL")
    }
    val mangaLists = remember {
        mutableStateListOf("MOTY", "Best Seinen")
    }
    AniHyouTheme {
        CustomListsContent(
            uiState = CustomListsUiState(
                animeLists = animeLists,
                mangaLists = mangaLists,
            ),
            event = null,
            navActionManager = NavActionManager.rememberNavActionManager()
        )
    }
}