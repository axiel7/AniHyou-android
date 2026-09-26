package com.axiel7.anihyou.feature.mediadetails

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class AiringNotificationType : Localizable {
    START,
    AIRING,
    END;

    @Composable
    override fun localized() = when (this) {
        START -> stringResource(R.string.allow_start_notification)
        AIRING -> stringResource(R.string.allow_airing_notification)
        END -> stringResource(R.string.allow_end_notification)
    }

    @get:DrawableRes
    val icon
        get() = when (this) {
            START -> R.drawable.calendar_today_24
            AIRING -> R.drawable.notifications_24
            END -> R.drawable.event_available_24
        }
}