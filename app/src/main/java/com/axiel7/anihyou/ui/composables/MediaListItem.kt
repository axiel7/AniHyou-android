package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun AiringAnimeHorizontalItem(
    title: String,
    subtitle: String,
    imageUrl: String?,
    score: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .sizeIn(maxWidth = 300.dp, minWidth = 250.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        MediaPoster(
            url = imageUrl,
            modifier = Modifier.size(width = MEDIA_POSTER_SMALL_WIDTH.dp, height = MEDIA_POSTER_SMALL_HEIGHT.dp)
        )

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                maxLines = 2,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (score != null) {
                SmallScoreIndicator(
                    score = score
                )
            }
        }
    }
}

@Composable
fun AiringAnimeHorizontalItemPlaceholder() {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .sizeIn(maxWidth = 300.dp, minWidth = 250.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .size(width = MEDIA_POSTER_SMALL_WIDTH.dp, height = MEDIA_POSTER_SMALL_HEIGHT.dp)
                .clip(RoundedCornerShape(8.dp))
                .defaultPlaceholder(visible = true)
        )

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = "This is a placeholder",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .defaultPlaceholder(visible = true)
            )
            Text(
                text = "This content is loading",
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .defaultPlaceholder(visible = true)
            )

            Text(
                text = "Loading",
                modifier = Modifier.defaultPlaceholder(visible = true)
            )
        }
    }
}

@Composable
fun SmallScoreIndicator(
    score: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.star_filled_20),
            contentDescription = stringResource(R.string.mean_score),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = score,
            modifier = Modifier.padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.outline,
            fontSize = fontSize
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AiringAnimeHorizontalItemPreview() {
    AniHyouTheme {
        AiringAnimeHorizontalItem(
            title = "Kimetsu no Yaiba: Katanakaji no Sato-hen",
            subtitle = "Airing in 12 min",
            imageUrl = null,
            score = "79%",
            onClick = { }
        )
    }
}