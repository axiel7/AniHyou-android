package com.axiel7.anihyou.core.ui.utils

import android.graphics.Typeface
import android.net.Uri
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

object StringUtils {

    fun String.htmlDecoded() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)

    /**
     * Converts a [Spanned] into an [AnnotatedString] trying to keep as much formatting as possible.
     *
     * Currently supports `bold`, `italic`, `underline` and `color`.
     */
    fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
        val spanned = this@toAnnotatedString
        append(spanned.toString())
        getSpans(0, spanned.length, Any::class.java).forEach { span ->
            val start = getSpanStart(span)
            val end = getSpanEnd(span)
            when (span) {
                is StyleSpan -> when (span.style) {
                    Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                    Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                    Typeface.BOLD_ITALIC -> addStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        ), start, end
                    )
                }

                is UnderlineSpan -> addStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline),
                    start,
                    end
                )

                is ForegroundColorSpan -> addStyle(
                    SpanStyle(color = Color(span.foregroundColor)),
                    start,
                    end
                )
            }
        }
    }

    fun String.urlEncode(): String? = Uri.encode(this)

    /**
     * Format the opening/ending text from MAL to use it on YouTube search
     */
    fun String.buildQueryFromThemeText() = this
        .replace("\"", "")
        .replaceFirst(Regex("#?\\w+:"), "") // theme number
        .replace(Regex("\\(ep.*\\)"), "") // episodes
        .trim()
        .urlEncode()
}