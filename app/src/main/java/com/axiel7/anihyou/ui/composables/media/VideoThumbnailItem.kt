package com.axiel7.anihyou.ui.composables.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.ui.theme.banner_shadow_color

const val VIDEO_SMALL_WIDTH = 193
const val VIDEO_SMALL_HEIGHT = 109

@Composable
fun VideoThumbnailItem(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .size(width = VIDEO_SMALL_WIDTH.dp, height = VIDEO_SMALL_HEIGHT.dp)
            .padding(start = 2.dp, end = 2.dp, bottom = 4.dp)
            .shadow(2.dp, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "video",
            modifier = Modifier.fillMaxSize(),
            placeholder = ColorPainter(MaterialTheme.colorScheme.outline),
            error = ColorPainter(MaterialTheme.colorScheme.outline),
            fallback = ColorPainter(MaterialTheme.colorScheme.outline),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(banner_shadow_color)
        )
        Icon(
            painter = painterResource(R.drawable.play_circle_40),
            contentDescription = "play",
            tint = Color.White
        )
    }
}

@Preview
@Composable
fun VideoThumbnailItemPreview() {
    AniHyouTheme {
        Surface {
            VideoThumbnailItem(
                imageUrl = null,
                onClick = {}
            )
        }
    }
}