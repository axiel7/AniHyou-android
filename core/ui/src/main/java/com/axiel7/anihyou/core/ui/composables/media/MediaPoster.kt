package com.axiel7.anihyou.core.ui.composables.media

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

const val MEDIA_POSTER_TINY_HEIGHT = 80
const val MEDIA_POSTER_TINY_WIDTH = 80

const val MEDIA_POSTER_COMPACT_HEIGHT = 115
const val MEDIA_POSTER_COMPACT_WIDTH = 80

const val MEDIA_POSTER_SMALL_HEIGHT = 140
const val MEDIA_POSTER_SMALL_WIDTH = 100

const val MEDIA_POSTER_MEDIUM_HEIGHT = 156
const val MEDIA_POSTER_MEDIUM_WIDTH = 110

const val MEDIA_POSTER_BIG_HEIGHT = 168
const val MEDIA_POSTER_BIG_WIDTH = 120

@Composable
fun MediaPoster(
    url: String?,
    showShadow: Boolean = true,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier
) {
    AsyncImage(
        model = url,
        contentDescription = "poster",
        placeholder = ColorPainter(MaterialTheme.colorScheme.outline),
        error = ColorPainter(MaterialTheme.colorScheme.outline),
        fallback = ColorPainter(MaterialTheme.colorScheme.outline),
        contentScale = contentScale,
        modifier = modifier
            .then(
                if (showShadow) Modifier
                    .padding(start = 2.dp, end = 2.dp, bottom = 4.dp)
                    .shadow(2.dp, shape = RoundedCornerShape(8.dp))
                else Modifier
            )
            .clip(RoundedCornerShape(8.dp))
    )
}

@Preview(showBackground = true)
@Composable
fun MediaPosterPreview() {
    AniHyouTheme {
        Surface {
            MediaPoster(
                url = "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx150672-2WWJVXIAOG11.png",
                modifier = Modifier
                    .size(
                        width = MEDIA_POSTER_SMALL_WIDTH.dp,
                        height = MEDIA_POSTER_SMALL_HEIGHT.dp
                    )
            )
        }
    }
}