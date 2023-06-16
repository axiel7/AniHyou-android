package com.axiel7.anihyou.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.RichTooltipBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberRichTooltipState
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
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = stringResource(R.string.on_my_list)) },
        modifier = Modifier.padding(horizontal = 8.dp),
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
    val tooltipState = rememberRichTooltipState(isPersistent = true)
    RichTooltipBox(
        text = {
            Text(text = description ?: stringResource(R.string.no_description))
        },
        tooltipState = tooltipState
    ) {
        ElevatedAssistChip(
            onClick = onClick,
            label = { Text(text = name) },
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .tooltipTrigger(),
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
    val tooltipState = rememberRichTooltipState(isPersistent = true)
    AnimatedVisibility(visible = visible) {
        RichTooltipBox(
            text = {
                Text(text = description ?: stringResource(R.string.no_description))
            },
            tooltipState = tooltipState
        ) {
            AssistChip(
                onClick = onClick,
                label = { Text(text = name) },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .tooltipTrigger(),
                leadingIcon = { Text(text = "${rank ?: 0}%") }
            )
        }
    }
}