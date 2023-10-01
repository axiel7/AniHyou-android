package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.utils.ContextUtils.openLink
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
fun OpenInBrowserIconButton(url: String) {
    val context = LocalContext.current
    IconButton(onClick = { context.openLink(url) }) {
        Icon(
            painter = painterResource(R.drawable.open_in_browser_24),
            contentDescription = "share",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FavoriteIconButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    favoritesCount: Int = 0,
    onClick: () -> Unit,
    fontSize: TextUnit = TextUnit.Unspecified,
    iconSize: Dp = 24.dp,
) {
    val tint = if (isFavorite) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface
    TextButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        if (favoritesCount > 0) {
            Text(
                text = favoritesCount.abbreviated(),
                color = tint,
                fontSize = fontSize
            )
        }
        Icon(
            painter = painterResource(
                if (isFavorite) R.drawable.favorite_filled_24
                else R.drawable.favorite_24
            ),
            contentDescription = "heart",
            modifier = Modifier
                .padding(start = 8.dp)
                .size(iconSize),
            tint = tint
        )
    }
}

@Composable
fun CommentIconButton(
    modifier: Modifier = Modifier,
    commentCount: Int = 0,
    onClick: () -> Unit,
    fontSize: TextUnit = TextUnit.Unspecified,
    iconSize: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        if (commentCount > 0) {
            Text(
                text = commentCount.abbreviated(),
                color = tint,
                fontSize = fontSize,
            )
        }
        Icon(
            painter = painterResource(R.drawable.chat_bubble_24),
            contentDescription = "chat_bubble",
            modifier = Modifier
                .padding(start = 8.dp)
                .size(iconSize),
            tint = tint
        )
    }
}