package com.axiel7.anihyou.feature.mediadetails.composables

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.sheet.SelectionSheet
import com.axiel7.anihyou.core.ui.composables.sheet.SelectionSheetItem
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.common.utils.ContextUtils.openActionView
import com.axiel7.anihyou.core.ui.utils.StringUtils.buildQueryFromThemeText

private enum class MusicStreaming(
    val searchUrl: String,
) : Localizable {
    YouTube("https://www.youtube.com/results?search_query="),
    Spotify("https://open.spotify.com/search/"),
    AppleMusic("https://music.apple.com/search?term="),
    YouTubeMusic("https://music.youtube.com/search?q="),
    Deezer("https://www.deezer.com/search/"),
    ;

    @Composable
    override fun localized() = when (this) {
        YouTube -> "YouTube"
        Spotify -> "Spotify"
        AppleMusic -> "Apple Music"
        YouTubeMusic -> "YouTube Music"
        Deezer -> "Deezer"
    }

    val icon: Int
        @DrawableRes
        get() = when (this) {
            YouTube -> R.drawable.youtube
            Spotify -> R.drawable.spotify
            AppleMusic -> R.drawable.apple_music
            YouTubeMusic -> R.drawable.youtube_music
            Deezer -> R.drawable.deezer
        }
}

@Composable
fun MusicStreamingSheet(
    songTitle: String,
    bottomPadding: Dp,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    SelectionSheet(
        bottomPadding = bottomPadding,
        onDismiss = onDismiss
    ) {
        MusicStreaming.entries.forEach { service ->
            SelectionSheetItem(
                name = service.localized(),
                icon = service.icon,
                iconTint = Color.Unspecified,
                onClick = {
                    context.openActionView(
                        service.searchUrl + songTitle.buildQueryFromThemeText()
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun MusicStreamingSheetPreview() {
    AniHyouTheme {
        MusicStreamingSheet(
            songTitle = "",
            bottomPadding = 0.dp,
            onDismiss = {}
        )
    }
}