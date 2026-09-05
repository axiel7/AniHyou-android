package com.axiel7.anihyou.core.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.axiel7.anihyou.core.resources.banner_shadow_color
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun CalendarBannerView(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    fallbackColor: Color? = null,
    gradientColor: Color? = null,
    height: Dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "banner",
                placeholder = ColorPainter(MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.None
            )
        } else {
            Box(
                modifier = Modifier
                    .background(
                        color = fallbackColor ?: MaterialTheme.colorScheme.outline
                    )
                    .fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(gradientColor ?: banner_shadow_color, MaterialTheme.colorScheme.surface),
                        start = Offset(x = 0f, y = Float.POSITIVE_INFINITY),
                        end = Offset(x = Float.POSITIVE_INFINITY, y = 0f)
                    )
                )
        )
    }
}

@Preview
@Composable
private fun CalendarBannerPreview() {
    AniHyouTheme {
        Surface {
            CalendarBannerView(
                imageUrl = null,
                fallbackColor = MaterialTheme.colorScheme.secondary,
                gradientColor = MaterialTheme.colorScheme.secondary,
                height = 150.dp
            )
        }
    }
}