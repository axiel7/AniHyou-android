package com.axiel7.anihyou.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class ChartType : Localizable {
    TOP_ANIME {
        @Composable
        override fun localized() = stringResource(R.string.top_100)
    },
    POPULAR_ANIME {
        @Composable
        override fun localized() = stringResource(R.string.top_popular)
    },
    UPCOMING_ANIME {
        @Composable
        override fun localized() = stringResource(R.string.upcoming)
    },
    AIRING_ANIME {
        @Composable
        override fun localized() = stringResource(R.string.airing)
    },
    TOP_MANGA {
        @Composable
        override fun localized() = stringResource(R.string.top_100)
    },
    POPULAR_MANGA {
        @Composable
        override fun localized() = stringResource(R.string.top_popular)
    },
    UPCOMING_MANGA {
        @Composable
        override fun localized() = stringResource(R.string.upcoming)
    },
    PUBLISHING_MANGA {
        @Composable
        override fun localized() = stringResource(R.string.publishing)
    },
}