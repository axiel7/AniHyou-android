package com.axiel7.anihyou.wear.ui.screens.usermedialist.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.tooling.preview.devices.WearDevices
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.core.model.media.progressOrVolumes
import com.axiel7.anihyou.core.resources.ColorUtils.colorFromHex
import com.axiel7.anihyou.wear.ui.composables.ScrollableColumn
import com.axiel7.anihyou.wear.ui.theme.AniHyouTheme
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
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier,
        positionIndicator = {
            PositionIndicator(
                scrollState = scrollState
            )
        },
        timeText = { TimeText() }
    ) {
        uiState.entry?.let { entry ->
            ScrollableColumn(
                scrollState = scrollState,
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = entry.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.title3,
                )

                val progress = entry.basicMediaListEntry.progressOrVolumes()?.format() ?: 0
                val duration = entry.duration()?.format()
                Text(
                    text = if (duration != null) "$progress/$duration" else "$progress",
                )

                Button(
                    onClick = { event?.onClickPlusOne() },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.primaryButtonColors(
                        backgroundColor = (colorFromHex(entry.media?.coverImage?.color)
                            ?: MaterialTheme.colors.primary)
                            .compositeOver(MaterialTheme.colors.background)
                    )
                ) {
                    Text(text = "+1")
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

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
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