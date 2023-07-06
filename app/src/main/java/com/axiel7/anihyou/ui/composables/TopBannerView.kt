package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.ui.theme.banner_shadow_color

@Composable
fun TopBannerView(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    fallbackColor: Color? = null,
    height: Dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "banner",
                placeholder = ColorPainter(MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
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
        //top shadow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(banner_shadow_color, MaterialTheme.colorScheme.surface)
                    )
                )
        )
    }
}

@Preview
@Composable
fun TopBannerViewPreview() {
    AniHyouTheme {
        Surface {
            TopBannerView(
                imageUrl = null,
                fallbackColor = MaterialTheme.colorScheme.secondary,
                height = 250.dp
            )
        }
    }
}