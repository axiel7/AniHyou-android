package com.axiel7.anihyou.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType

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
    };

    val mediaType
        get() = when (this) {
            TOP_ANIME,
            POPULAR_ANIME,
            UPCOMING_ANIME,
            AIRING_ANIME -> MediaType.ANIME

            TOP_MANGA,
            POPULAR_MANGA,
            UPCOMING_MANGA,
            PUBLISHING_MANGA -> MediaType.MANGA
        }

    val mediaSort
        get() = when (this) {
            TOP_ANIME,
            TOP_MANGA,
            AIRING_ANIME,
            PUBLISHING_MANGA -> MediaSort.SCORE_DESC

            POPULAR_ANIME,
            UPCOMING_ANIME,
            POPULAR_MANGA,
            UPCOMING_MANGA -> MediaSort.POPULARITY_DESC
        }

    val mediaStatus
        get() = when (this) {
            UPCOMING_ANIME,
            UPCOMING_MANGA -> MediaStatus.NOT_YET_RELEASED

            AIRING_ANIME,
            PUBLISHING_MANGA -> MediaStatus.RELEASING

            else -> null
    }
}