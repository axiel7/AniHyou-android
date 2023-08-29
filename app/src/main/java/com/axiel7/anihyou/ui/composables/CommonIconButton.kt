package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.utils.ContextUtils.openShareSheet
import com.axiel7.anihyou.utils.NumberUtils.abbreviated

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
    favoritesCount: Int = 0,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (favoritesCount > 0) {
            Text(
                text = favoritesCount.abbreviated(),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
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
}