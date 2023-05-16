package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaItemHorizontal(
    title: String,
    imageUrl: String?,
    score: Int,
    format: MediaFormat,
    year: Int?,
    onClick: () -> Unit,
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.height(MEDIA_POSTER_SMALL_HEIGHT.dp)
        ) {
            MediaPoster(
                url = imageUrl,
                showShadow = false,
                modifier = Modifier
                    .size(
                        width = MEDIA_POSTER_SMALL_WIDTH.dp,
                        height = MEDIA_POSTER_SMALL_HEIGHT.dp
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )

                Text(
                    text = buildString {
                        append(format.localized())
                        if (year != null) append(" · $year")
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SmallScoreIndicator(
                    score = "$score%",
                    fontSize = 15.sp
                )

            }//: Column
        }//: Row
    }//: Card
}

@Composable
fun MediaItemHorizontalPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(MEDIA_POSTER_SMALL_HEIGHT.dp)
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = MEDIA_POSTER_SMALL_WIDTH.dp,
                    height = MEDIA_POSTER_SMALL_HEIGHT.dp
                )
                .clip(RoundedCornerShape(8.dp))
                .defaultPlaceholder(visible = true)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "This is a placeholder text",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontSize = 17.sp,
            )

            Text(
                text = "Placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true),
            )
            Text(
                text = "Placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true),
            )
        }//: Column
    }//: Row
}

@Preview
@Composable
fun MediaItemHorizontalPreview() {
    AniHyouTheme {
        Surface {
            Column {
                MediaItemHorizontal(
                    title = "This is a very large anime title that should serve as a preview example",
                    imageUrl = null,
                    score = 76,
                    format = MediaFormat.TV,
                    year = 2014,
                    onClick = {}
                )
                MediaItemHorizontalPlaceholder()
            }
        }
    }
}