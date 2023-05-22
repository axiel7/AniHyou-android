package com.axiel7.anihyou.ui.composables.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun MediaActivityItem(
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(MEDIA_POSTER_TINY_HEIGHT.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        MediaPoster(
            url = imageUrl,
            modifier = Modifier.size(MEDIA_POSTER_TINY_HEIGHT.dp),
            showShadow = false
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 16.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 4.dp),
                lineHeight = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )

            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MediaActivityItemPlaceholder(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(MEDIA_POSTER_TINY_HEIGHT.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(MEDIA_POSTER_TINY_HEIGHT.dp)
                .defaultPlaceholder(visible = true)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 16.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "This is a  loading placeholder",
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .defaultPlaceholder(visible = true)
            )

            Text(
                text = "Placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontSize = 14.sp,
            )
        }
    }
}

@Preview
@Composable
fun MediaActivityItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                MediaActivityItem(
                    title = "Plans to watch Alice to Therese no Maboroshi Koujou",
                    imageUrl = null,
                    modifier = Modifier.padding(8.dp),
                    subtitle = "14 h ago",
                    onClick = {}
                )
                MediaActivityItemPlaceholder(
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}