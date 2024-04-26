package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R

@Composable
fun RandomEntryButton(
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text = stringResource(R.string.random)) },
        modifier = Modifier.padding(horizontal = 16.dp),
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.shuffle_24),
                contentDescription = stringResource(R.string.random)
            )
        }
    )
}