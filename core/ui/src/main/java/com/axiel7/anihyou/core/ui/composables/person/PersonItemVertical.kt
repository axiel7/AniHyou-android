package com.axiel7.anihyou.core.ui.composables.person

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun PersonItemVertical(
    title: String,
    modifier: Modifier = Modifier,
    imageUrl: String?,
    subtitle: String? = null,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PersonImage(
            url = imageUrl,
            modifier = Modifier
                .size(PERSON_IMAGE_SIZE_SMALL.dp),
            showShadow = true
        )

        Text(
            text = title,
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )

        subtitle?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }//: Row
}

@Composable
fun PersonItemVerticalPlaceholder() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(PERSON_IMAGE_SIZE_SMALL.dp)
                .clip(CircleShape)
                .defaultPlaceholder(visible = true)
        )


        Text(
            text = "A placeholder",
            modifier = Modifier
                .padding(top = 8.dp)
                .defaultPlaceholder(visible = true),
            fontSize = 17.sp,
            maxLines = 1
        )

        Text(
            text = "Placeholder",
            modifier = Modifier
                .padding(top = 4.dp)
                .defaultPlaceholder(visible = true),
            fontSize = 14.sp,
            maxLines = 1
        )
    }//: Row
}

@Preview
@Composable
fun PersonItemVerticalPreview() {
    AniHyouTheme {
        Surface {
            Column {
                PersonItemVertical(
                    title = "Asano Inio",
                    imageUrl = null,
                    subtitle = "Original Author",
                    onClick = {}
                )
                PersonItemVerticalPlaceholder()
            }
        }
    }
}