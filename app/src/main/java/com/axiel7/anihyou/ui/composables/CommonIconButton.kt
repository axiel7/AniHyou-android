package com.axiel7.anihyou.ui.composables

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.axiel7.anihyou.R

@Composable
fun BackIconButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.arrow_back_24),
            contentDescription = "back",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}