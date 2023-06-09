package com.axiel7.anihyou.ui.composables.person

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import coil.compose.AsyncImage
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val PERSON_IMAGE_SIZE_VERY_SMALL = 32
const val PERSON_IMAGE_SIZE_SMALL = 80
const val PERSON_IMAGE_SIZE_BIG = 124

@Composable
fun PersonImage(
    url: String?,
    modifier: Modifier,
    showShadow: Boolean = false,
    contentScale: ContentScale = ContentScale.Crop,
) {
    AsyncImage(
        model = url,
        contentDescription = "profile",
        placeholder = ColorPainter(MaterialTheme.colorScheme.outline),
        error = ColorPainter(MaterialTheme.colorScheme.outline),
        fallback = ColorPainter(MaterialTheme.colorScheme.outline),
        contentScale = contentScale,
        modifier = modifier
            .then(
                if (showShadow) Modifier
                    .padding(start = 2.dp, end = 2.dp, bottom = 4.dp)
                    .shadow(2.dp, shape = CircleShape)
                else Modifier
            )
            .clip(CircleShape)
    )
}

@Preview
@Composable
fun PersonImagePreview() {
    AniHyouTheme {
        Surface {
            PersonImage(
                url = null,
                modifier = Modifier
                    .size(PERSON_IMAGE_SIZE_SMALL.dp),
                showShadow = true
            )
        }
    }
}