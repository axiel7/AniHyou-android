package com.axiel7.anihyou.data.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType

enum class ChartType : Localizable {
    TOP_ANIME,
    POPULAR_ANIME,
    UPCOMING_ANIME,
    AIRING_ANIME,
    TOP_MOVIES,
    TOP_MANGA,
    POPULAR_MANGA,
    UPCOMING_MANGA,
    PUBLISHING_MANGA;

    @Composable
    override fun localized() = when (this) {
        TOP_ANIME -> stringResource(R.string.top_100)
        POPULAR_ANIME -> stringResource(R.string.top_popular)
        UPCOMING_ANIME -> stringResource(R.string.upcoming)
        AIRING_ANIME -> stringResource(R.string.airing)
        TOP_MOVIES -> stringResource(R.string.top_movies)
        TOP_MANGA -> stringResource(R.string.top_100)
        POPULAR_MANGA -> stringResource(R.string.top_popular)
        UPCOMING_MANGA -> stringResource(R.string.upcoming)
        PUBLISHING_MANGA -> stringResource(R.string.publishing)
    }

    val mediaType
        get() = when (this) {
            TOP_ANIME,
            POPULAR_ANIME,
            UPCOMING_ANIME,
            AIRING_ANIME,
            TOP_MOVIES -> MediaType.ANIME

            TOP_MANGA,
            POPULAR_MANGA,
            UPCOMING_MANGA,
            PUBLISHING_MANGA -> MediaType.MANGA
        }

    val mediaSort
        get() = when (this) {
            TOP_ANIME,
            TOP_MOVIES,
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

    val mediaFormat
        get() = when (this) {
            TOP_MOVIES -> MediaFormat.MOVIE
            else -> null
        }
}