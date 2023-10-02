package com.axiel7.anihyou.ui.screens.home.activity.composables

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R

@Composable
fun ActivityFollowingChip(
    value: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    AssistChip(
        onClick = { onValueChanged(!value) },
        label = {
            Text(
                text = stringResource(
                    if (value) R.string.following else R.string.global_feed
                )
            )
        }
    )
}