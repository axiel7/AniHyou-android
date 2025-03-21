package com.axiel7.anihyou.core.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.network.type.MediaStatus
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

@Composable
fun MediaStatus?.localized() = when (this) {
    MediaStatus.FINISHED -> stringResource(R.string.finished)
    MediaStatus.RELEASING -> stringResource(R.string.releasing)
    MediaStatus.NOT_YET_RELEASED -> stringResource(R.string.not_yet_released)
    MediaStatus.CANCELLED -> stringResource(R.string.cancelled)
    MediaStatus.HIATUS -> stringResource(R.string.hiatus)
    else -> stringResource(R.string.unknown)
}

enum class MediaStatusLocalizable(val value: MediaStatus) : Localizable {
    FINISHED(MediaStatus.FINISHED),
    RELEASING(MediaStatus.RELEASING),
    NOT_YET_RELEASED(MediaStatus.NOT_YET_RELEASED),
    CANCELLED(MediaStatus.CANCELLED),
    HIATUS(MediaStatus.HIATUS);

    @Composable
    override fun localized() = value.localized()
}