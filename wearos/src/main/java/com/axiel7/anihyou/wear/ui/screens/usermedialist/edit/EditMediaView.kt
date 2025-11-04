package com.axiel7.anihyou.wear.ui.screens.usermedialist.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.material3.CircularProgressIndicator
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.ScrollIndicator
import androidx.wear.compose.material3.TextButton
import androidx.wear.compose.material3.TextButtonDefaults
import androidx.wear.compose.material3.contentColorFor
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.core.model.media.progressOrVolumes
import com.axiel7.anihyou.core.resources.ColorUtils.colorFromHex
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.wear.ui.composables.ScrollableColumn
import com.axiel7.anihyou.wear.ui.theme.AniHyouTheme
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditMediaView(modifier: Modifier = Modifier) {
    val viewModel: EditMediaViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    EditMediaContent(
        uiState = uiState,
        event = viewModel,
        modifier = modifier,
    )
}

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun EditMediaContent(
    uiState: EditMediaUiState,
    event: EditMediaEvent?,
    modifier: Modifier = Modifier
) {
    val contentPadding = rememberResponsiveColumnPadding(
        first = ColumnItemType.ListHeader,
        last = ColumnItemType.Button,
    )
    val scrollState = rememberScrollState()

    ScreenScaffold(
        modifier = modifier,
        positionIndicator = {
            Box(modifier = Modifier.fillMaxSize()) {
                ScrollIndicator(
                    state = scrollState,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        },
        scrollState = scrollState,
    ) {
        uiState.entry?.let { entry ->
            val accentColor = (colorFromHex(entry.media?.coverImage?.color)
                ?: MaterialTheme.colorScheme.primary).compositeOver(MaterialTheme.colorScheme.background)

            ScrollableColumn(
                scrollState = scrollState,
                modifier = Modifier.padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ListHeader(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    Text(
                        text = entry.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 4,
                    )
                }

                val progress = entry.basicMediaListEntry.progressOrVolumes()?.format() ?: 0
                val duration = entry.duration()?.format()
                Text(
                    text = if (duration != null) "$progress/$duration" else "$progress",
                )

                TextButton(
                    onClick = { event?.onClickPlusOne() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 4.dp),
                    colors = TextButtonDefaults.filledTextButtonColors(
                        containerColor = accentColor,
                        contentColor = contentColorFor(accentColor)
                    ),
                    shapes = TextButtonDefaults.animatedShapes(),
                ) {
                    Text(text = stringResource(R.string.plus_one))
                }

                TextButton(
                    onClick = { event?.onClickMinusOne() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 4.dp),
                    colors = TextButtonDefaults.outlinedTextButtonColors(contentColor = accentColor),
                    border = BorderStroke(1.dp, accentColor),
                    shapes = TextButtonDefaults.animatedShapes(),
                ) {
                    Text(text = stringResource(R.string.minus_one))
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
private fun EditMediaPreview() {
    AniHyouTheme {
        EditMediaContent(
            uiState = EditMediaUiState(
                entry = exampleCommonMediaListEntry
            ),
            event = null,
        )
    }
}