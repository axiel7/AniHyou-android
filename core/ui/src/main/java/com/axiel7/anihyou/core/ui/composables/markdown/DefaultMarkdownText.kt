package com.axiel7.anihyou.core.ui.composables.markdown

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.ui.composables.sheet.ModalBottomSheet
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.MarkdownUtils.formatCompatibleMarkdown
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography

@Composable
fun DefaultMarkdownText(
    markdown: String?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = LocalTextStyle.current.fontSize,
    lineHeight: TextUnit = LocalTextStyle.current.lineHeight,
    uriHandler: MarkdownUriHandler,
) {
    CompositionLocalProvider(LocalUriHandler provides uriHandler) {
        Markdown(
            content = markdown?.formatCompatibleMarkdown().orEmpty(),
            typography = markdownTypography(
                text = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                )
            ),
            modifier = modifier,
            imageTransformer = Coil3ImageTransformerImpl,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpoilerSheet(
    text: String,
    uriHandler: MarkdownUriHandler,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissed = onDismiss,
        sheetState = rememberBottomSheetState(
            initialValue = SheetValue.Hidden,
            enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
        )
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        ) {
            DefaultMarkdownText(
                markdown = text,
                uriHandler = uriHandler,
            )
        }
    }
}

@Preview
@Composable
private fun DefaultMarkdownTextPreview() {
    AniHyouTheme {
        Surface {
            DefaultMarkdownText(
                markdown = "",
                uriHandler = MarkdownUriHandler()
            )
        }
    }
}