package com.axiel7.anihyou.feature.notifications.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.ui.composables.activity.ACTIVITY_IMAGE_SIZE
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.media.MediaPoster
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

const val NOTIFICATION_IMAGE_SIZE = 48

@Composable
fun NotificationItem(
    title: String,
    modifier: Modifier = Modifier,
    imageUrl: String?,
    subtitle: String?,
    isUnread: Boolean,
    onClick: () -> Unit,
    onClickImage: () -> Unit = {},
) {
    Row(
        modifier = (if (isUnread) Modifier.background(MaterialTheme.colorScheme.surfaceVariant) else Modifier)
            .then(modifier)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        MediaPoster(
            url = imageUrl,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(ACTIVITY_IMAGE_SIZE.dp)
                .clickable(onClick = onClickImage),
            showShadow = false
        )

        Column(
            modifier = Modifier
                .padding(end = 8.dp),
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
fun NotificationItemPlaceholder(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(NOTIFICATION_IMAGE_SIZE.dp)
                .defaultPlaceholder(visible = true)
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "This is a  loading placeholder",
                modifier = Modifier
                    .padding(bottom = 8.dp)
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
fun ActivityItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                NotificationItem(
                    title = "Plans to watch Alice to Therese no Maboroshi Koujou",
                    imageUrl = "",
                    modifier = Modifier.padding(8.dp),
                    subtitle = "14 h ago",
                    isUnread = true,
                    onClick = {}
                )
                NotificationItemPlaceholder(
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}