package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R

@Composable
fun NotesIndicator(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Icon(
        painter = painterResource(R.drawable.notes_20),
        contentDescription = stringResource(R.string.notes),
        modifier = modifier
            .clickable(onClick = onClick),
    )
}