package com.axiel7.anihyou.core.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.network.type.MediaFormat
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

@Composable
fun MediaFormat.localized() = when (this) {
    MediaFormat.TV -> stringResource(R.string.tv)
    MediaFormat.TV_SHORT -> stringResource(R.string.tv_short)
    MediaFormat.MOVIE -> stringResource(R.string.movie)
    MediaFormat.SPECIAL -> stringResource(R.string.special)
    MediaFormat.OVA -> stringResource(R.string.ova)
    MediaFormat.ONA -> stringResource(R.string.ona)
    MediaFormat.MUSIC -> stringResource(R.string.music)
    MediaFormat.MANGA -> stringResource(R.string.manga)
    MediaFormat.NOVEL -> stringResource(R.string.novel)
    MediaFormat.ONE_SHOT -> stringResource(R.string.one_shot)
    MediaFormat.UNKNOWN__ -> stringResource(R.string.unknown)
}

enum class MediaFormatLocalizable(val value: MediaFormat) : Localizable {
    TV(MediaFormat.TV),
    TV_SHORT(MediaFormat.TV_SHORT),
    MOVIE(MediaFormat.MOVIE),
    SPECIAL(MediaFormat.SPECIAL),
    OVA(MediaFormat.OVA),
    ONA(MediaFormat.ONA),
    MUSIC(MediaFormat.MUSIC),
    MANGA(MediaFormat.MANGA),
    NOVEL(MediaFormat.NOVEL),
    ONE_SHOT(MediaFormat.ONE_SHOT);

    @Composable
    override fun localized() = value.localized()

    companion object {
        val animeEntries = listOf(TV, TV_SHORT, MOVIE, SPECIAL, OVA, ONA, MUSIC)
        val mangaEntries = listOf(MANGA, NOVEL, ONE_SHOT)
    }
}