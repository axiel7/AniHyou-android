package com.axiel7.anihyou.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.dynamicColorScheme

@Composable
fun AniHyouTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = dynamicColorScheme(LocalContext.current) ?: MaterialTheme.colorScheme,
        content = content
    )
}