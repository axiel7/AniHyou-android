package com.axiel7.anihyou.ui.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.ui.composables.generateHtml
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.openActionView
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData

@Composable
fun ProfileAboutView(
    aboutHtml: String?
) {
    if (aboutHtml == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        val context = LocalContext.current
        val html = generateHtml(html = aboutHtml)
        val webViewState = rememberWebViewStateWithHTMLData(data = html)
        val webClient = remember {
            object : AccompanistWebViewClient() {
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
            state = webViewState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            captureBackPresses = false,
            onCreated = { webView ->
                webView.background = ColorDrawable(Color.TRANSPARENT)
            },
            client = webClient
        )
    }
}

@Preview
@Composable
fun ProfileAboutViewPreview() {
    AniHyouTheme {
        Surface {
            ProfileAboutView(
                aboutHtml = "<p>こんにちは！アクです。</p>\n" +
                        "<blockquote>\n" +
                        "<p>Developing an iOS AniList client:<br />\n" +
                        "<a href=\"https://github.com/axiel7/AniHyou\">https://github.com/axiel7/AniHyou</a></p>\n" +
                        "</blockquote>"
            )
        }
    }
}