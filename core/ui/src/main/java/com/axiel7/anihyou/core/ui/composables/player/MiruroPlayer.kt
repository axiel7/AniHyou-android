package com.axiel7.anihyou.core.ui.composables.player

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.axiel7.anihyou.core.base.MIRURO_BASE_URL

/**
 * Full Miruro embedded player.
 * Opens miruro.to/watch?id={anilistId}&ep={episode}&dub={isDub} inside a WebView.
 * The user never leaves the app.
 */
@Composable
fun MiruroPlayer(
    anilistId: Int,
    episode: Int,
    isDub: Boolean,
    modifier: Modifier = Modifier,
) {
    val url = remember(anilistId, episode, isDub) {
        buildMiruroUrl(anilistId, episode, isDub)
    }

    var currentUrl by remember { mutableStateOf(url) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    allowFileAccess = false
                    allowContentAccess = false
                    // Allow fullscreen video
                    setSupportMultipleWindows(false)
                }
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                loadUrl(url)
            }
        },
        update = { webView ->
            val newUrl = buildMiruroUrl(anilistId, episode, isDub)
            if (webView.url != newUrl) {
                webView.loadUrl(newUrl)
            }
        },
        modifier = modifier.fillMaxSize(),
    )
}

fun buildMiruroUrl(anilistId: Int, episode: Int, isDub: Boolean): String {
    val lang = if (isDub) "dub" else "sub"
    return "$MIRURO_BASE_URL/watch?id=$anilistId&ep=$episode&lang=$lang"
}
