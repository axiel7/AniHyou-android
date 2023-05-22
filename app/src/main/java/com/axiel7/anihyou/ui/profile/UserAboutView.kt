package com.axiel7.anihyou.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.HtmlWebView
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun UserAboutView(
    aboutHtml: String?,
    isLoading: Boolean = false,
) {
    if (aboutHtml != null) {
        HtmlWebView(html = aboutHtml)
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading)
                CircularProgressIndicator()
            else
                Text(text = stringResource(R.string.no_description))
        }
    }
}

@Preview
@Composable
fun UserAboutViewPreview() {
    AniHyouTheme {
        Surface {
            UserAboutView(
                aboutHtml = "<p>こんにちは！アクです。</p>\n" +
                        "<blockquote>\n" +
                        "<p>Developing an iOS AniList client:<br />\n" +
                        "<a href=\"https://github.com/axiel7/AniHyou\">https://github.com/axiel7/AniHyou</a></p>\n" +
                        "</blockquote>"
            )
        }
    }
}