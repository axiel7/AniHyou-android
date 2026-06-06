package com.axiel7.anihyou.core.ui.composables.player

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.axiel7.anihyou.core.base.VIDFAST_MOVIE_URL
import com.axiel7.anihyou.core.base.VIDFAST_TV_URL

/**
 * VidFast embedded player.
 *
 * FOR PERSONAL USE ONLY. VidFast aggregates streams from third-party sites.
 * Do not distribute this feature in a public release.
 *
 * Usage:
 *   // For a movie (use TMDB or IMDB id):
 *   VidFastPlayer(mediaId = "533535", mediaType = VidFastMediaType.MOVIE)
 *
 *   // For a TV episode:
 *   VidFastPlayer(
 *       mediaId = "63174",
 *       mediaType = VidFastMediaType.TV,
 *       season = 1,
 *       episode = 5,
 *   )
 *
 * The player requires INTERNET permission (already in the manifest).
 * Hardware acceleration is required; ensure it is enabled in AndroidManifest.xml
 * for the activity (it is by default in AOSP).
 */

enum class VidFastMediaType { MOVIE, TV }

@Composable
fun VidFastPlayer(
    mediaId: String,
    mediaType: VidFastMediaType,
    season: Int = 1,
    episode: Int = 1,
    autoPlay: Boolean = true,
    theme: String? = null,           // hex color without '#', e.g. "16A085"
    showNextButton: Boolean = true,
    autoNext: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val url = remember(mediaId, mediaType, season, episode, autoPlay, theme) {
        buildVidFastUrl(mediaId, mediaType, season, episode, autoPlay, theme, showNextButton, autoNext)
    }

    var isLoading by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        contentAlignment = Alignment.Center,
    ) {
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
                    }
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }
                    }
                    webChromeClient = WebChromeClient()
                    loadUrl(url)
                }
            },
            update = { webView ->
                if (webView.url != url) {
                    isLoading = true
                    webView.loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}

private fun buildVidFastUrl(
    mediaId: String,
    mediaType: VidFastMediaType,
    season: Int,
    episode: Int,
    autoPlay: Boolean,
    theme: String?,
    showNextButton: Boolean,
    autoNext: Boolean,
): String {
    val base = when (mediaType) {
        VidFastMediaType.MOVIE -> "$VIDFAST_MOVIE_URL/$mediaId"
        VidFastMediaType.TV -> "$VIDFAST_TV_URL/$mediaId/$season/$episode"
    }

    val params = buildList {
        if (autoPlay) add("autoPlay=true")
        theme?.let { add("theme=$it") }
        if (mediaType == VidFastMediaType.TV) {
            if (showNextButton) add("nextButton=true")
            if (autoNext) add("autoNext=true")
        }
    }

    return if (params.isEmpty()) base else "$base?${params.joinToString("&")}"
}
