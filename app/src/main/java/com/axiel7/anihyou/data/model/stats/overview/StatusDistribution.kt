package com.axiel7.anihyou.data.model.stats.overview

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.MediaStatsQuery
import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.stats.StatLocalizableAndColorable
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.ui.theme.stat_dark_blue
import com.axiel7.anihyou.ui.theme.stat_dark_green
import com.axiel7.anihyou.ui.theme.stat_dark_onBlue
import com.axiel7.anihyou.ui.theme.stat_dark_onGreen
import com.axiel7.anihyou.ui.theme.stat_dark_onRed
import com.axiel7.anihyou.ui.theme.stat_dark_onYellow
import com.axiel7.anihyou.ui.theme.stat_dark_red
import com.axiel7.anihyou.ui.theme.stat_dark_yellow
import com.axiel7.anihyou.ui.theme.stat_light_blue
import com.axiel7.anihyou.ui.theme.stat_light_green
import com.axiel7.anihyou.ui.theme.stat_light_onBlue
import com.axiel7.anihyou.ui.theme.stat_light_onGreen
import com.axiel7.anihyou.ui.theme.stat_light_onRed
import com.axiel7.anihyou.ui.theme.stat_light_onYellow
import com.axiel7.anihyou.ui.theme.stat_light_red
import com.axiel7.anihyou.ui.theme.stat_light_yellow

enum class StatusDistribution(
    val status: MediaListStatus,
) : Localizable, Colorable {
    CURRENT(MediaListStatus.CURRENT),
    COMPLETED(MediaListStatus.COMPLETED),
    PAUSED(MediaListStatus.PAUSED),
    DROPPED(MediaListStatus.DROPPED),
    PLANNING(MediaListStatus.PLANNING);

    @Composable
    override fun primaryColor(): Color {
        val isDark = isSystemInDarkTheme()
        return when (status) {
            MediaListStatus.CURRENT -> if (isDark) stat_dark_green else stat_light_green
            MediaListStatus.PLANNING -> MaterialTheme.colorScheme.outline
            MediaListStatus.COMPLETED -> if (isDark) stat_dark_blue else stat_light_blue
            MediaListStatus.DROPPED -> if (isDark) stat_dark_red else stat_light_red
            MediaListStatus.PAUSED -> if (isDark) stat_dark_yellow else stat_light_yellow
            MediaListStatus.REPEATING -> if (isDark) stat_dark_blue else stat_light_blue
            MediaListStatus.UNKNOWN__ -> MaterialTheme.colorScheme.outline
        }
    }

    @Composable
    override fun onPrimaryColor(): Color {
        val isDark = isSystemInDarkTheme()
        return when (status) {
            MediaListStatus.CURRENT -> if (isDark) stat_dark_onGreen else stat_light_onGreen
            MediaListStatus.PLANNING -> if (isDark) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.inverseOnSurface
            MediaListStatus.COMPLETED -> if (isDark) stat_dark_onBlue else stat_light_onBlue
            MediaListStatus.DROPPED -> if (isDark) stat_dark_onRed else stat_light_onRed
            MediaListStatus.PAUSED -> if (isDark) stat_dark_onYellow else stat_light_onYellow
            MediaListStatus.REPEATING -> if (isDark) stat_dark_onBlue else stat_light_onBlue
            MediaListStatus.UNKNOWN__ -> MaterialTheme.colorScheme.onSurface
        }
    }

    @Composable
    override fun localized() = status.localized()

    companion object {
        fun valueOf(rawValue: String?) = entries.find { it.status.rawValue == rawValue }

        fun MediaListStatus.asStat() = when (this) {
            MediaListStatus.CURRENT -> CURRENT
            MediaListStatus.PLANNING -> PLANNING
            MediaListStatus.COMPLETED -> COMPLETED
            MediaListStatus.DROPPED -> DROPPED
            MediaListStatus.PAUSED -> PAUSED
            MediaListStatus.REPEATING -> CURRENT
            MediaListStatus.UNKNOWN__ -> null
        }

        fun MediaStatsQuery.StatusDistribution.asStat(): StatLocalizableAndColorable<StatusDistribution>? {
            val status = valueOf(status?.rawValue)
            return if (status != null) {
                StatLocalizableAndColorable(
                    type = status,
                    value = amount?.toFloat() ?: 0f
                )
            } else null
        }
    }
}
