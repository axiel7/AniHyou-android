package com.axiel7.anihyou.core.ui.composables.webview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WebView(
    data: String,
    modifier: Modifier = Modifier,
    hardwareEnabled: Boolean = true,
    onCreated: (WebView) -> Unit = {},
    onDispose: (WebView) -> Unit = {},
    client: WebViewClient = remember { WebViewClient() },
    chromeClient: WebChromeClient? = null,
    factory: ((Context) -> WebView)? = null,
) {
    BoxWithConstraints(modifier) {
        // WebView changes it's layout strategy based on
        // it's layoutParams. We convert from Compose Modifier to
        // layout params here.
        val width =
            if (this.constraints.hasFixedWidth)
                ViewGroup.LayoutParams.MATCH_PARENT
            else
                ViewGroup.LayoutParams.WRAP_CONTENT
        val height =
            if (this.constraints.hasFixedHeight)
                ViewGroup.LayoutParams.MATCH_PARENT
            else
                ViewGroup.LayoutParams.WRAP_CONTENT

        val layoutParams = FrameLayout.LayoutParams(
            width,
            height
        )

        WebView(
            data,
            layoutParams,
            Modifier,
            hardwareEnabled,
            onCreated,
            onDispose,
            client,
            chromeClient,
            factory
        )
    }
}

@Composable
fun WebView(
    data: String,
    layoutParams: FrameLayout.LayoutParams,
    modifier: Modifier = Modifier,
    hardwareEnabled: Boolean = true,
    onCreated: (WebView) -> Unit = {},
    onDispose: (WebView) -> Unit = {},
    client: WebViewClient = remember { WebViewClient() },
    chromeClient: WebChromeClient? = null,
    factory: ((Context) -> WebView)? = null,
) {
    AndroidView(
        factory = { context ->
            (factory?.invoke(context) ?: WebView(context)).apply {
                // weird workaround for a rare crash of WebView with Compose Navigation animations
                this.alpha = 0.99f

                onCreated(this)
                if (!hardwareEnabled) setLayerType(View.LAYER_TYPE_SOFTWARE, null)

                this.layoutParams = layoutParams

                chromeClient?.let { webChromeClient = it }
                webViewClient = client

                loadDataWithBaseURL(null, data, null, "utf-8", null)
            }
        },
        modifier = modifier,
        onRelease = {
            // this also seems to fix the crash with Navigation
            it.visibility = View.INVISIBLE

            onDispose(it)
            it.stopLoading()
            it.destroy()
        }
    )
}