package com.axiel7.anihyou.wear.ui.screens.usermedialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CardDefaults
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.OutlinedCompactChip
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.placeholder
import androidx.wear.compose.material.placeholderShimmer
import androidx.wear.compose.material.rememberPlaceholderState
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import coil3.compose.AsyncImage
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.model.media.progressOrVolumes
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.ColorUtils.colorFromHex
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.wear.ui.composables.OnBottomReached
import com.axiel7.anihyou.wear.ui.theme.AniHyouTheme
import com.google.android.horologist.compose.layout.ScreenScaffold
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UserMediaListView(
    mediaType: MediaType,
    goToEditMedia: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: UserMediaListViewModel = koinViewModel(
        key = mediaType.name,
        parameters = { parametersOf(mediaType) }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UserMediaListContent(
        uiState = uiState,
        event = viewModel,
        modifier = modifier,
        goToEditMedia = goToEditMedia,
    )
}

@Composable
private fun UserMediaListContent(
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    modifier: Modifier = Modifier,
    goToEditMedia: (Int) -> Unit = {},
) {
    val listState = rememberScalingLazyListState()
    listState.OnBottomReached(buffer = 2) {
        event?.onLoadMore()
    }

    val canScrollUp by remember {
        derivedStateOf { listState.canScrollBackward }
    }

    ScreenScaffold(
        modifier = modifier,
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = listState
            )
        },
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
        ) {
            item {
                Box(modifier = Modifier.height(48.dp)) {
                    AnimatedVisibility(
                        visible = !canScrollUp,
                        enter = fadeIn() + slideInVertically(tween(100)),
                        exit = fadeOut() + slideOutVertically(tween(100)),
                    ) {
                        OutlinedCompactChip(
                            onClick = { event?.refreshList() },
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.refresh_24),
                                    contentDescription = stringResource(R.string.refresh)
                                )
                            }
                        )
                    }
                }
            }
            item {
                LaunchedEffect(uiState.entries) {
                    if (uiState.entries.isEmpty()) {
                        listState.scrollToItem(index = 1, scrollOffset = 50)
                    }
                }
                Text(
                    text = uiState.mediaType.localized(),
                    modifier = Modifier.padding(bottom = 4.dp),
                    style = MaterialTheme.typography.title3
                )
            }
            items(uiState.entries) { item ->
                ItemView(
                    item = item,
                    onClick = { goToEditMedia(item.mediaId) }
                )
            }
            if (uiState.isLoading) {
                items(10) {
                    ItemPlaceholder()
                }
            }
        }
    }
}

@Composable
private fun ItemView(
    item: CommonMediaListEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(IntrinsicSize.Min),
        contentColor = MaterialTheme.colors.onSurface,
        contentPadding = PaddingValues(),
    ) {
        Box(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier.matchParentSize(),
            ) {
                AsyncImage(
                    model = item.media?.coverImage?.large,
                    contentDescription = "background",
                    placeholder = ColorPainter(MaterialTheme.colors.surface),
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .paint(
                            CardDefaults.cardBackgroundPainter(
                                startBackgroundColor = (colorFromHex(item.media?.coverImage?.color)
                                    ?: MaterialTheme.colors.primary.copy(alpha = 0.50f))
                                    .compositeOver(MaterialTheme.colors.background),
                                endBackgroundColor = MaterialTheme.colors.background.copy(
                                    alpha = 0.50f
                                )
                            )
                        )
                )
            }
            Column(
                modifier = Modifier
                    .padding(CardDefaults.ContentPadding)
            ) {
                Text(
                    text = item.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                )
                val progress = item.basicMediaListEntry.progressOrVolumes()?.format() ?: 0
                val duration = item.duration()?.format()
                Text(
                    text = if (duration != null) "$progress/$duration" else "$progress",
                    fontSize = 15.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
private fun ItemPlaceholder(modifier: Modifier = Modifier) {
    val placeholderState = rememberPlaceholderState { false }
    LaunchedEffect(placeholderState) {
        placeholderState.startPlaceholderAnimation()
    }
    Card(
        onClick = {},
        modifier = modifier.placeholderShimmer(placeholderState)
    ) {
        Text(
            text = "Loading...",
            modifier = Modifier
                .placeholder(placeholderState)
                .padding(horizontal = 2.dp, vertical = 1.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Loading",
            modifier = Modifier
                .placeholder(placeholderState)
                .padding(horizontal = 2.dp, vertical = 1.dp)
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
private fun UserMediaListPreview() {
    val exampleEntries = remember {
        mutableStateListOf(
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
        )
    }
    AniHyouTheme {
        UserMediaListContent(
            uiState = UserMediaListUiState(
                mediaType = MediaType.ANIME,
                entries = exampleEntries
            ),
            event = null
        )
    }
}