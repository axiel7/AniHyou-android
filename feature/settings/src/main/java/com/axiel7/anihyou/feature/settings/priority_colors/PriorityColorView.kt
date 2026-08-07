package com.axiel7.anihyou.feature.settings.priority_colors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.CommonColorPickerDialog
import com.axiel7.anihyou.core.ui.composables.common.SmallCircularProgressIndicator
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
        actions = {
            if (uiState.isLoading) {
                SmallCircularProgressIndicator()
            } else {
                IconButton(onClick = { event?.updateColors(uiState.lowPriorityColor, uiState.mediumPriorityColor, uiState.highPriorityColor) }) {
                    Icon(
                        painter = painterResource(R.drawable.save_24),
                        contentDescription = stringResource(R.string.save)
                    )
                }
            }
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            PriorityColorItem(
                title = stringResource(R.string.priority_high),
                color = uiState.highPriorityColor,
                onClick = { showColorPickerByPriority = 2 }
            )
            PriorityColorItem(
                title = stringResource(R.string.priority_medium),
                color = uiState.mediumPriorityColor,
                onClick = { showColorPickerByPriority = 1 }
            )
            PriorityColorItem(
                title = stringResource(R.string.priority_low),
                color = uiState.lowPriorityColor,
                onClick = { showColorPickerByPriority = 0 }
            )
        }
    }

    // color picker
    if (showColorPickerByPriority != null) {
        val priority = showColorPickerByPriority!!
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

// don't know where to put this
@Composable
private fun PriorityColorItem(
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        trailingContent = {
            Box(modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    ) {
        Text(text = title)
    }
}
