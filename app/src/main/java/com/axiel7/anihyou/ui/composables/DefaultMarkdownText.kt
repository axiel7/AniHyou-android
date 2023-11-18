package com.axiel7.anihyou.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import coil.imageLoader
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.openActionView
import com.axiel7.anihyou.utils.MarkdownUtils.formatCompatibleMarkdown
import com.axiel7.anihyou.utils.MarkdownUtils.onMarkdownLinkClicked
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun DefaultMarkdownText(
    markdown: String?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    color: Color = MaterialTheme.colorScheme.onSurface,
    navigateToFullscreenImage: (String) -> Unit = {},
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
    MarkdownText(
        markdown = markdown?.formatCompatibleMarkdown().orEmpty(),
        modifier = modifier,
        fontSize = fontSize,
        lineHeight = lineHeight,
        color = color,
        linkColor = MaterialTheme.colorScheme.primary,
        onLinkClicked = { link ->
            link.onMarkdownLinkClicked(
                onSpoilerClicked = { spoilerText = it },
                onLinkClicked = { context.openActionView(it) },
                onImageClicked = navigateToFullscreenImage
            )
        },
        imageLoader = context.imageLoader
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
                markdown = "",
            )
        }
    }
}