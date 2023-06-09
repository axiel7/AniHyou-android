package com.axiel7.anihyou.ui.composables.thread

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.DefaultMarkdownText
import com.axiel7.anihyou.ui.composables.SpoilerDialog
import com.axiel7.anihyou.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.openActionView
import com.axiel7.anihyou.utils.DateUtils.timestampToDateString
import com.axiel7.anihyou.utils.NumberUtils.format

@Composable
fun ThreadCommentView(
    body: String,
    username: String,
    likeCount: Int,
    createdAt: Int,
    navigateToFullscreenImage: (String) -> Unit,
) {
    val context = LocalContext.current
    var spoilerText by remember { mutableStateOf<String?>(null) }

    spoilerText?.let {
        SpoilerDialog(
            text = it,
            onDismiss = {
                spoilerText = null
            }
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = username,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = createdAt.toLong().timestampToDateString(format = "MMM d, YYYY") ?: "",
                color = MaterialTheme.colorScheme.outline,
                fontSize = 15.sp
            )
        }
        DefaultMarkdownText(
            markdown = body,
            modifier = Modifier.padding(vertical = 8.dp),
            fontSize = 16.sp,
            onImageClicked = navigateToFullscreenImage,
            onSpoilerClicked = { spoilerText = it },
            onLinkClicked = { context.openActionView(it) }
        )
        TextIconHorizontal(
            text = likeCount.format(),
            icon = R.drawable.favorite_20
        )
    }
}

@Composable
fun ThreadCommentViewPlaceholder() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Loading",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Loading",
                modifier = Modifier.defaultPlaceholder(visible = true),
                color = MaterialTheme.colorScheme.outline,
                fontSize = 15.sp
            )
        }
        Text(
            text = "This is a loading placeholder of a comment.",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .defaultPlaceholder(visible = true),
            fontSize = 18.sp,
        )
        TextIconHorizontal(
            text = "17",
            icon = R.drawable.favorite_20,
            modifier = Modifier.defaultPlaceholder(visible = true)
        )
    }
}

@Preview
@Composable
fun ThreadCommentViewPreview() {
    AniHyouTheme {
        Surface {
            Column {
                ThreadCommentView(
                    body = "Yet again, even more peak",
                    username = "Lap",
                    likeCount = 23,
                    createdAt = 1212370032,
                    navigateToFullscreenImage = {}
                )
                ThreadCommentViewPlaceholder()
            }
        }
    }
}