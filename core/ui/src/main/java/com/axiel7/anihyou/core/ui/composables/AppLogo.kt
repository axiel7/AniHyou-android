package com.axiel7.anihyou.core.ui.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.axiel7.anihyou.core.resources.R

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
) {
    val logoRes = if (isSystemInDarkTheme()) {
        R.drawable.ic_logo_dark
    } else {
        R.drawable.ic_logo_light
    }
    IconCard(
        icon = logoRes,
        modifier = modifier,
    )
}
