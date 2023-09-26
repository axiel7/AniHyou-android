package com.axiel7.anihyou.ui.composables.webview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.axiel7.anihyou.utils.ColorUtils.hexToString
import com.axiel7.anihyou.utils.ContextUtils.openActionView

@Composable
fun HtmlWebView(
    html: String,
    modifier: Modifier = Modifier,
    hardwareEnabled: Boolean = true,
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val htmlConverted by remember {
        derivedStateOf { generateHtml(html, colorScheme) }
    }
    val webClient = remember {
        object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                request?.url?.let { context.openActionView(it) }
                return true
            }
        }
    }

    WebView(
        data = htmlConverted,
        modifier = modifier.fillMaxWidth(),
        hardwareEnabled = hardwareEnabled,
        onCreated = { webView ->
            webView.background = ColorDrawable(Color.TRANSPARENT)
            webView.isScrollContainer = false
            webView.isVerticalScrollBarEnabled = false
        },
        client = webClient
    )
}

fun generateHtml(
    html: String,
    colorScheme: ColorScheme
) = """
    <HTML>
    <head>
        <meta name='viewport' content='width=device-width, shrink-to-fit=YES, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'>
    </head>
    ${generateCSS(colorScheme)}
    <BODY>
    <div id="anihyou">${formatCompatibleHtml(html)}</div>
    </BODY>
    </HTML>
""".trimIndent()

fun formatCompatibleHtml(html: String): String {
    return html
        // replace AniList markdown [text](link) with html <a>
        .replace(Regex("\\[([^]]+)]\\(([^)]+)\\)"), "<a href=\"\$2\">\$1</a>")
        // replace AniList markdown __bold__ with html <b>
        .replace(Regex("__(.+)__"), "<b>\$1</b>")
        // escaped chars
        .replace("&lt;", "<")
        .replace("&gt;", ">")
}

fun generateCSS(colorScheme: ColorScheme): String {
    return """
    <style type='text/css'>
        ${
        baseCss(
            backgroundColor = colorScheme.background.toArgb().hexToString(),
            fontColor = colorScheme.onBackground.toArgb().hexToString(),
            linkColor = colorScheme.primary.toArgb().hexToString()
        )
    }
        body {
            margin: 16;
            padding: 0;
        }
    </style>
    """.trimIndent()
}

fun baseCss(
    backgroundColor: String,
    fontColor: String,
    linkColor: String
) = """
    body {background-color: $backgroundColor;}
    img{max-height: 100%; min-height: 100%; height:auto; max-width: 100%; width:auto;margin-bottom:5px; border-radius: 8px;}
    h1, h2, h3, h4, h5, h6, p, div, dl, ol, ul, pre, blockquote {text-align:left; line-height: 170%; font-family: 'Arial' !important; color: $fontColor; }
    iframe{width:100%; height:250px;}
    a:link {color: $linkColor;}
    A {text-decoration: none;}
    .markdown_spoiler {color: $fontColor; background-color: $fontColor;}
    .markdown_spoiler:not(:hover):not(:focus):not(:active) a:link {color: $fontColor;}
    .markdown_spoiler:hover, .markdown_spoiler:focus, .markdown_spoiler:active {background-color: transparent;}
""".trimIndent()