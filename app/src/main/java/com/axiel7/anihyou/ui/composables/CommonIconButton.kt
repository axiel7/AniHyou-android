package com.axiel7.anihyou.ui.composables

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.utils.ContextUtils.openShareSheet

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

@Composable
fun ShareIconButton(url: String) {
    val context = LocalContext.current
    IconButton(onClick = { context.openShareSheet(url) }) {
        Icon(
            painter = painterResource(R.drawable.share_24),
            contentDescription = "share",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FavoriteIconButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(
                if (isFavorite) R.drawable.favorite_filled_24
                else R.drawable.favorite_24
            ),
            contentDescription = "heart",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}