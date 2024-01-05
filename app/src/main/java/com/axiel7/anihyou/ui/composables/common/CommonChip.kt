package com.axiel7.anihyou.ui.composables.common

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ChipElevation
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.utils.NumberUtils.format
import kotlinx.coroutines.launch

@Composable
fun FilterSelectionChip(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = text) },
        modifier = modifier,
        leadingIcon = {
            if (selected) {
                Icon(painter = painterResource(R.drawable.check_24), contentDescription = "check")
            }
        }
    )
}

@Composable
fun TriFilterChip(
    text: String,
    value: Boolean?,
    onValueChanged: (Boolean?) -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = value != null,
        onClick = {
            onValueChanged(
                when (value) {
                    null -> true
                    true -> false
                    false -> null
                }
            )
        },
        label = { Text(text = text) },
        modifier = modifier,
        leadingIcon = {
            if (value == true) {
                Icon(painter = painterResource(R.drawable.check_24), contentDescription = "check")
            } else if (value == false) {
                Icon(painter = painterResource(R.drawable.close_24), contentDescription = "close")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistChipWithTooltip(
    label: String,
    modifier: Modifier = Modifier,
    tooltipContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = AssistChipDefaults.shape,
    colors: ChipColors = AssistChipDefaults.assistChipColors(),
    elevation: ChipElevation? = AssistChipDefaults.assistChipElevation(),
    border: BorderStroke? = AssistChipDefaults.assistChipBorder(enabled),
) {
    val scope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState(isPersistent = true)

    val showTooltip: () -> Unit = {
        if (tooltipContent != null) scope.launch { tooltipState.show() }
    }

    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            RichTooltip(text = tooltipContent ?: {})
        },
        state = tooltipState,
        modifier = modifier,
        enableUserInput = tooltipContent != null
    ) {
        AssistChip(
            onClick = onClick ?: showTooltip,
            label = { Text(text = label) },
            enabled = enabled,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagChip(
    name: String,
    description: String?,
    rank: Int?,
    onClick: () -> Unit
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            RichTooltip {
                Text(text = description ?: stringResource(R.string.no_description))
            }
        },
        state = tooltipState
    ) {
        ElevatedAssistChip(
            onClick = onClick,
            label = { Text(text = name) },
            modifier = Modifier
                .padding(horizontal = 4.dp),
            leadingIcon = { Text(text = "${(rank ?: 0).format()}%") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpoilerTagChip(
    name: String,
    description: String?,
    rank: Int?,
    visible: Boolean,
    onClick: () -> Unit
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    AnimatedVisibility(visible = visible) {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                RichTooltip {
                    Text(text = description ?: stringResource(R.string.no_description))
                }
            },
            state = tooltipState
        ) {
            AssistChip(
                onClick = onClick,
                label = { Text(text = name) },
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                leadingIcon = { Text(text = "${(rank ?: 0).format()}%") }
            )
        }
    }
}

@Composable
fun InputChipError(
    selected: Boolean = false,
    onClick: () -> Unit,
    text: String,
    @DrawableRes icon: Int?,
    iconDescription: String?,
) {
    InputChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = text) },
        trailingIcon = icon?.let {
            {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = iconDescription
                )
            }
        },
        colors = InputChipDefaults.inputChipColors(
            labelColor = MaterialTheme.colorScheme.error
        )
    )
}