package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.type.MediaSort

@Composable
fun MediaSort.localized() = when (this) {
    MediaSort.ID -> stringResource(R.string.id)
    MediaSort.ID_DESC -> stringResource(R.string.id)
    MediaSort.TITLE_ROMAJI -> stringResource(R.string.title_romaji)
    MediaSort.TITLE_ROMAJI_DESC -> stringResource(R.string.title_romaji)
    MediaSort.TITLE_ENGLISH -> stringResource(R.string.title_english)
    MediaSort.TITLE_ENGLISH_DESC -> stringResource(R.string.title_english)
    MediaSort.TITLE_NATIVE -> stringResource(R.string.title_native)
    MediaSort.TITLE_NATIVE_DESC -> stringResource(R.string.title_native)
    MediaSort.TYPE -> stringResource(R.string.sort_type)
    MediaSort.TYPE_DESC -> stringResource(R.string.sort_type)
    MediaSort.FORMAT -> stringResource(R.string.format)
    MediaSort.FORMAT_DESC -> stringResource(R.string.format)
    MediaSort.START_DATE -> stringResource(R.string.start_date)
    MediaSort.START_DATE_DESC -> stringResource(R.string.start_date)
    MediaSort.END_DATE -> stringResource(R.string.end_date)
    MediaSort.END_DATE_DESC -> stringResource(R.string.end_date)
    MediaSort.SCORE -> stringResource(R.string.sort_score)
    MediaSort.SCORE_DESC -> stringResource(R.string.sort_score)
    MediaSort.POPULARITY -> stringResource(R.string.popularity)
    MediaSort.POPULARITY_DESC -> stringResource(R.string.popularity)
    MediaSort.TRENDING -> stringResource(R.string.trending)
    MediaSort.TRENDING_DESC -> stringResource(R.string.trending)
    MediaSort.EPISODES -> stringResource(R.string.episodes)
    MediaSort.EPISODES_DESC -> stringResource(R.string.episodes)
    MediaSort.DURATION -> stringResource(R.string.duration)
    MediaSort.DURATION_DESC -> stringResource(R.string.duration)
    MediaSort.STATUS -> stringResource(R.string.sort_status)
    MediaSort.STATUS_DESC -> stringResource(R.string.sort_status)
    MediaSort.CHAPTERS -> stringResource(R.string.chapters)
    MediaSort.CHAPTERS_DESC -> stringResource(R.string.chapters)
    MediaSort.VOLUMES -> stringResource(R.string.volumes)
    MediaSort.VOLUMES_DESC -> stringResource(R.string.volumes)
    MediaSort.UPDATED_AT -> stringResource(R.string.sort_updated)
    MediaSort.UPDATED_AT_DESC -> stringResource(R.string.sort_updated)
    MediaSort.SEARCH_MATCH -> stringResource(R.string.search_match)
    MediaSort.FAVOURITES -> stringResource(R.string.favorites)
    MediaSort.FAVOURITES_DESC -> stringResource(R.string.favorites)
    MediaSort.UNKNOWN__ -> stringResource(R.string.unknown)
}

enum class MediaSortSearch(
    val asc: MediaSort,
    val desc: MediaSort
): Localizable {
    SEARCH_MATCH(
        asc = MediaSort.SEARCH_MATCH,
        desc = MediaSort.SEARCH_MATCH,
    ) {
        @Composable override fun localized() = stringResource(R.string.sort_default)
    },
    POPULARITY(
        asc = MediaSort.POPULARITY,
        desc = MediaSort.POPULARITY_DESC,
    ) {
        @Composable override fun localized() = asc.localized()
    },
    SCORE(
        asc = MediaSort.SCORE,
        desc = MediaSort.SCORE_DESC,
    ) {
        @Composable override fun localized() = asc.localized()
    },
    TRENDING(
        asc = MediaSort.TRENDING,
        desc = MediaSort.TRENDING_DESC,
    ) {
        @Composable override fun localized() = asc.localized()
    },
    FAVOURITES(
        asc = MediaSort.FAVOURITES,
        desc = MediaSort.FAVOURITES_DESC,
    ) {
        @Composable override fun localized() = asc.localized()
    },
    START_DATE(
        asc = MediaSort.START_DATE,
        desc = MediaSort.START_DATE_DESC,
    ) {
        @Composable override fun localized() = asc.localized()
    };

    companion object {
        fun valueOf(value: MediaSort) = MediaSortSearch.values().find {
            it.desc == value || it.asc == value
        }
    }
}