package com.axiel7.anihyou.core.ui.composables.person

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun PersonItemHorizontal(
    title: String,
    modifier: Modifier = Modifier,
    imageUrl: String?,
    imageSize: Dp = PERSON_IMAGE_SIZE_SMALL.dp,
    subtitle: String? = null,
    onClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonImage(
                url = imageUrl,
                modifier = Modifier
                    .size(imageSize),
                showShadow = true
            )

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                subtitle?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
fun PersonItemHorizontalMirrored(
    title: String,
    modifier: Modifier = Modifier,
    imageUrl: String?,
    imageSize: Dp = PERSON_IMAGE_SIZE_SMALL.dp,
    subtitle: String? = null,
    onClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    fontSize = 17.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                subtitle?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            PersonImage(
                url = imageUrl,
                modifier = Modifier
                    .size(imageSize),
                showShadow = true
            )
        }
    }
}

@Composable
fun PersonItemHorizontalPlaceholder() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(PERSON_IMAGE_SIZE_SMALL.dp)
                .clip(CircleShape)
                .defaultPlaceholder(visible = true)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "This is a placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
            )

            Text(
                text = "Placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }//: Column
    }//: Row
}

@Preview
@Composable
private fun PersonItemHorizontalPreview() {
    AniHyouTheme {
        Surface {
            Column {
                PersonItemHorizontal(
                    title = "Asano Inio",
                    imageUrl = null,
                    subtitle = "Original Author",
                    onClick = {}
                )
                PersonItemHorizontalMirrored(
                    title = "Asano Inio",
                    imageUrl = null,
                    subtitle = "Original Author",
                    onClick = {}
                )
                PersonItemHorizontalPlaceholder()
            }
        }
    }
}