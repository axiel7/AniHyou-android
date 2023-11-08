package com.axiel7.anihyou.ui.screens.profile.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.webview.HtmlWebView
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun UserAboutView(
    aboutHtml: String?,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    navigateToUserMediaList: ((MediaType) -> Unit)?,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        if (navigateToUserMediaList != null) {
            Column(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FilledTonalButton(
                    onClick = { navigateToUserMediaList(MediaType.ANIME) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.live_tv_24),
                        contentDescription = stringResource(R.string.anime),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = stringResource(R.string.view_anime_list))
                }

                FilledTonalButton(
                    onClick = { navigateToUserMediaList(MediaType.MANGA) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.book_24),
                        contentDescription = stringResource(R.string.manga),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = stringResource(R.string.view_manga_list))
                }
            }
        }
        if (aboutHtml != null) {
            HtmlWebView(
                html = aboutHtml
            )
        } else {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading)
                    CircularProgressIndicator()
                else
                    Text(text = stringResource(R.string.no_description))
            }
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
                        "</blockquote>",
                navigateToUserMediaList = {}
            )
        }
    }
}