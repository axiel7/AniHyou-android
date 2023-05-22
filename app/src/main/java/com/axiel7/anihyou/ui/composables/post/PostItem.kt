package com.axiel7.anihyou.ui.composables.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val POST_ITEM_HEIGHT = 144
const val POST_ITEM_WIDTH = 300

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItem(
    title: String,
    author: String,
    modifier: Modifier = Modifier,
    subtitle: @Composable () -> Unit = {},
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier.size(width = POST_ITEM_WIDTH.dp, height = POST_ITEM_HEIGHT.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                subtitle()

                Text(text = author)
            }
        }
    }
}

@Composable
fun PostItemPlaceholder(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.size(width = POST_ITEM_WIDTH.dp, height = POST_ITEM_HEIGHT.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "This is a loading placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true)
            )
            Text(
                text = "This is a loading placeholder, wait",
                modifier = Modifier.defaultPlaceholder(visible = true)
            )
            Text(
                text = "A placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Placeholder",
                    modifier = Modifier.defaultPlaceholder(visible = true)
                )

                Text(
                    text = "Placeholder",
                    modifier = Modifier.defaultPlaceholder(visible = true)
                )
            }
        }
    }
}

@Preview
@Composable
fun PostItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                PostItem(
                    title = "Love letter to art, culture, and excellence: the Manga. And some more text to test this view",
                    author = "Hannelore",
                    modifier = Modifier.padding(16.dp),
                    subtitle = {
                        Text(text = "97/100")
                    },
                    onClick = {}
                )
                PostItemPlaceholder(modifier = Modifier.padding(16.dp))
            }
        }
    }
}