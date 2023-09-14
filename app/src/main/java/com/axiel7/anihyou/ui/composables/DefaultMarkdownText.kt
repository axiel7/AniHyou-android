package com.axiel7.anihyou.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import coil.imageLoader
import com.axiel7.anihyou.App
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.MarkdownUtils.formatCompatibleMarkdown
import com.axiel7.anihyou.utils.MarkdownUtils.onMarkdownLinkClicked
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun DefaultMarkdownText(
    markdown: String?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onSpoilerClicked: (String) -> Unit = {},
    onLinkClicked: (String) -> Unit = {}
) {
    MarkdownText(
        markdown = markdown?.formatCompatibleMarkdown() ?: "",
        modifier = modifier,
        fontSize = fontSize,
        color = color,
        onLinkClicked = { link ->
            link.onMarkdownLinkClicked(
                onSpoilerClicked = onSpoilerClicked,
                onLinkClicked = onLinkClicked
            )
        },
        imageLoader = App.applicationContext.imageLoader
    )
}

@Composable
fun SpoilerDialog(
    text: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.close))
            }
        },
        text = {
            Text(text = text)
        }
    )
}

@Preview
@Composable
fun DefaultMarkdownTextPreview() {
    AniHyouTheme {
        Surface {
            DefaultMarkdownText(
                markdown = ""
            )
        }
    }
}