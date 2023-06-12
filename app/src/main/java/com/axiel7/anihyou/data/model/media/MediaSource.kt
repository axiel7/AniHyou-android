package com.axiel7.anihyou.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaSource

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