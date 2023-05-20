package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import com.axiel7.anihyou.data.model.base.LocalizableAndColorable
import com.axiel7.anihyou.type.MediaListStatus

enum class StatusDistribution(
    val value: MediaListStatus
) : LocalizableAndColorable {
    CURRENT(MediaListStatus.CURRENT),
    COMPLETED(MediaListStatus.COMPLETED),
    PAUSED(MediaListStatus.PAUSED),
    DROPPED(MediaListStatus.DROPPED),
    PLANNING(MediaListStatus.PLANNING);

    @Composable override fun primaryColor() = value.colorScheme().primary
    @Composable override fun onPrimaryColor() = value.colorScheme().onPrimary
    @Composable override fun localized() = value.localized()

    companion object {
        fun valueOf(rawValue: String?) = StatusDistribution.values().find { it.value.rawValue == rawValue }
    }
}
