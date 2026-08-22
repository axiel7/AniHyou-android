package com.axiel7.anihyou.feature.settings.priority_colors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.CommonColorPickerDialog
import com.axiel7.anihyou.core.ui.composables.preferenceShape
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PriorityColorView() {
    val viewModel: PriorityColorViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    PriorityColorContent(
        uiState = uiState,
        event = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityColorContent(
    uiState: PriorityColorUiState,
    event: PriorityColorEvent?
) {
    var showColorPickerByPriority by remember { mutableStateOf<Int?>(null) }
    val navActionManager = LocalNavActionManager.current
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.priority_color_change),
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        scrollBehavior = topAppBarScrollBehavior
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            val count = 3
            PriorityColorItem(
                title = stringResource(R.string.priority_high),
                color = uiState.highPriorityColor,
                shape = preferenceShape(0, count),
                onClick = { showColorPickerByPriority = 2 }
            )
            PriorityColorItem(
                title = stringResource(R.string.priority_medium),
                color = uiState.mediumPriorityColor,
                shape = preferenceShape(1, count),
                onClick = { showColorPickerByPriority = 1 }
            )
            PriorityColorItem(
                title = stringResource(R.string.priority_low),
                color = uiState.lowPriorityColor,
                shape = preferenceShape(2, count),
                onClick = { showColorPickerByPriority = 0 }
            )
        }
    }

    // color picker
    showColorPickerByPriority?.let { priority ->
        val initialColor = when (priority) {
            2 -> uiState.highPriorityColor
            1 -> uiState.mediumPriorityColor
            else -> uiState.lowPriorityColor
        }
        val title = when (priority) {
            2 -> stringResource(R.string.priority_high)
            1 -> stringResource(R.string.priority_medium)
            else -> stringResource(R.string.priority_low)
        }

        CommonColorPickerDialog(
            title = title,
            initialColor = initialColor,
            onDismissRequest = { showColorPickerByPriority = null },
            onColorSelected = { color ->
                when (priority) {
                    2 -> event?.onHighPriorityColorChanged(color)
                    1 -> event?.onMediumPriorityColorChanged(color)
                    0 -> event?.onLowPriorityColorChanged(color)
                }
            }
        )
    }
}

@Composable
private fun PriorityColorItem(
    title: String,
    color: Color,
    shape: Shape,
    onClick: () -> Unit
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
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = title)

            Box(modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color)
            )
        }
    }
}

@Preview
@Composable
private fun PriorityColorPreview() {
    AniHyouTheme {
        PriorityColorContent(
            uiState = PriorityColorUiState(),
            event = null,
        )
    }
}
