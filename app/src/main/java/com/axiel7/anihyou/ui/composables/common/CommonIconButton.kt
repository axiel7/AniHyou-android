package com.axiel7.anihyou.ui.composables.common

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.utils.ContextUtils.openInGoogleTranslate
import com.axiel7.anihyou.utils.ContextUtils.openLink
import com.axiel7.anihyou.utils.ContextUtils.openShareSheet
import com.axiel7.anihyou.utils.NumberUtils.abbreviated
import com.axiel7.anihyou.utils.NumberUtils.format

@Composable
fun BackIconButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.arrow_back_24),
            contentDescription = stringResource(R.string.action_back),
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
            contentDescription = stringResource(R.string.share),
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
            contentDescription = stringResource(R.string.external_links),
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
                text = favoritesCount.abbreviated().orEmpty(),
                color = tint,
                fontSize = fontSize
            )
        }
        Icon(
            painter = painterResource(
                if (isFavorite) R.drawable.favorite_filled_24
                else R.drawable.favorite_24
            ),
            contentDescription = stringResource(R.string.favorites),
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
                text = commentCount.abbreviated().orEmpty(),
                color = tint,
                fontSize = fontSize,
            )
        }
        Icon(
            painter = painterResource(R.drawable.chat_bubble_24),
            contentDescription = stringResource(R.string.comments),
            modifier = Modifier
                .padding(start = 8.dp)
                .size(iconSize),
            tint = tint
        )
    }
}

@Composable
fun ReplyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    fontSize: TextUnit = TextUnit.Unspecified,
    iconSize: Dp = 24.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.reply_24),
            contentDescription = stringResource(R.string.reply),
            modifier = Modifier
                .padding(end = 8.dp)
                .size(iconSize),
            tint = tint
        )
        Text(
            text = stringResource(R.string.reply),
            color = tint,
            fontSize = fontSize,
        )
    }
}

@Composable
fun TranslateIconButton(
    text: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            text?.let { context.openInGoogleTranslate(it) }
        },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.translate_24),
            contentDescription = stringResource(R.string.translate)
        )
    }
}

@Composable
fun LikeButton(
    isLiked: Boolean,
    modifier: Modifier = Modifier,
    likeCount: Int = 0,
    isDislike: Boolean = false,
    onClick: () -> Unit,
) {
    val icon = when {
        isDislike && isLiked -> R.drawable.thumb_down_filled_24
        isDislike -> R.drawable.thumb_down_24
        isLiked -> R.drawable.thumb_up_filled_24
        else -> R.drawable.thumb_up_24
    }
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        if (likeCount > 0) {
            Text(text = likeCount.format().orEmpty())
        }
        Icon(
            painter = painterResource(icon),
            contentDescription = "like"
        )
    }
}