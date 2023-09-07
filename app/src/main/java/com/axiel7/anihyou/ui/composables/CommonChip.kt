package com.axiel7.anihyou.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R

@OptIn(ExperimentalMaterial3Api::class)
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
        modifier = modifier.padding(horizontal = 4.dp),
        leadingIcon = {
            if (selected) {
                Icon(painter = painterResource(R.drawable.check_24), contentDescription = "check")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnMyListChip(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = stringResource(R.string.on_my_list)) },
        modifier = modifier,
        leadingIcon = {
            if (selected) {
                Icon(painter = painterResource(R.drawable.check_24), contentDescription = "check")
            }
        }
    )
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
            leadingIcon = { Text(text = "${rank ?: 0}%") }
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
                leadingIcon = { Text(text = "${rank ?: 0}%") }
            )
        }
    }
}