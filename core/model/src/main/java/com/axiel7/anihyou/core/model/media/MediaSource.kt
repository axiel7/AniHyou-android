package com.axiel7.anihyou.core.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.network.type.MediaSource
import com.axiel7.anihyou.core.resources.R

@Composable
fun MediaSource.localized() = when (this) {
    MediaSource.ORIGINAL -> stringResource(R.string.original)
    MediaSource.MANGA -> stringResource(R.string.manga)
    MediaSource.LIGHT_NOVEL -> stringResource(R.string.light_novel)
    MediaSource.VISUAL_NOVEL -> stringResource(R.string.visual_novel)
    MediaSource.VIDEO_GAME -> stringResource(R.string.video_game)
    MediaSource.OTHER -> stringResource(R.string.other)
    MediaSource.NOVEL -> stringResource(R.string.novel)
    MediaSource.DOUJINSHI -> stringResource(R.string.doujinshi)
    MediaSource.ANIME -> stringResource(R.string.anime)
    MediaSource.WEB_NOVEL -> stringResource(R.string.web_novel)
    MediaSource.LIVE_ACTION -> stringResource(R.string.live_action)
    MediaSource.GAME -> stringResource(R.string.game)
    MediaSource.COMIC -> stringResource(R.string.comic)
    MediaSource.MULTIMEDIA_PROJECT -> stringResource(R.string.multimedia_project)
    MediaSource.PICTURE_BOOK -> stringResource(R.string.picture_book)
    MediaSource.UNKNOWN__ -> stringResource(R.string.unknown)
}

enum class MediaSourceLocalizable(val value: MediaSource): Localizable {
    ORIGINAL(MediaSource.ORIGINAL),
    MANGA(MediaSource.MANGA),
    LIGHT_NOVEL(MediaSource.LIGHT_NOVEL),
    VISUAL_NOVEL(MediaSource.VISUAL_NOVEL),
    VIDEO_GAME(MediaSource.VIDEO_GAME),
    OTHER(MediaSource.OTHER),
    NOVEL(MediaSource.NOVEL),
    DOUJINSHI(MediaSource.DOUJINSHI),
    ANIME(MediaSource.ANIME),
    WEB_NOVEL(MediaSource.WEB_NOVEL),
    LIVE_ACTION(MediaSource.LIVE_ACTION),
    GAME(MediaSource.GAME),
    COMIC(MediaSource.COMIC),
    MULTIMEDIA_PROJECT(MediaSource.MULTIMEDIA_PROJECT),
    PICTURE_BOOK(MediaSource.PICTURE_BOOK),
    UNKNOWN(MediaSource.UNKNOWN__),
    ;

    @Composable
    override fun localized() = value.localized()
}