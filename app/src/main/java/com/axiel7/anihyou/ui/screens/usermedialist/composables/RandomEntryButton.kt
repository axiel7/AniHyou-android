package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R

@Composable
fun RandomEntryButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text = stringResource(R.string.random)) },
        modifier = modifier,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.shuffle_24),
                contentDescription = stringResource(R.string.random)
            )
        }
    )
}