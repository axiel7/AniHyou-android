package com.axiel7.anihyou.core.domain.model

import com.axiel7.anihyou.core.database.savedfilters.SavedFilterEntity
import com.axiel7.anihyou.core.model.SearchType
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.network.type.MediaFormat
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaSource
import com.axiel7.anihyou.core.network.type.MediaStatus

data class SavedFilter(
    val id: Int,
    val name: String,
    val searchType: SearchType,
    val mediaSort: MediaSort?,
    val genreIn: List<String>?,
    val genreNot: List<String>?,
    val tagIn: List<String>?,
    val tagNot: List<String>?,
    val minimumTagPercentage: Int,
    val mediaFormats: List<MediaFormat>?,
    val mediaStatuses: List<MediaStatus>?,
    val startYear: Int?,
    val endYear: Int?,
    val season: MediaSeason?,
    val episodesChaptersRange: IntRange?,
    val durationVolumesRange: IntRange?,
    val onMyList: Boolean?,
    val isDoujin: Boolean?,
    val isAdult: Boolean?,
    val country: CountryOfOrigin?,
    val sources: List<MediaSource>?,
) {
    fun toEntity() = SavedFilterEntity(
        id = id,
        name = name,
        searchType = searchType.name,
        mediaSort = mediaSort?.name,
        genreIn = genreIn,
        genreNot = genreNot,
        tagIn = tagIn,
        tagNot = tagNot,
        minimumTagPercentage = minimumTagPercentage,
        mediaFormats = mediaFormats?.map { it.name },
        mediaStatuses = mediaStatuses?.map { it.name },
        startYear = startYear,
        endYear = endYear,
        season = season?.name,
        episodesChaptersRange = episodesChaptersRange,
        durationVolumesRange = durationVolumesRange,
        onMyList = onMyList,
        isDoujin = isDoujin,
        isAdult = isAdult,
        country = country?.name,
        sources = sources?.map { it.name },
    )

    companion object {
        fun fromEntity(entity: SavedFilterEntity) = SavedFilter(
            id = entity.id,
            name = entity.name,
            searchType = SearchType.valueOf(entity.searchType),
            mediaSort = entity.mediaSort?.let { MediaSort.valueOf(it) },
            genreIn = entity.genreIn,
            genreNot = entity.genreNot,
            tagIn = entity.tagIn,
            tagNot = entity.tagNot,
            minimumTagPercentage = entity.minimumTagPercentage,
            mediaFormats = entity.mediaFormats?.map { MediaFormat.valueOf(it) },
            mediaStatuses = entity.mediaStatuses?.map { MediaStatus.valueOf(it) },
            startYear = entity.startYear,
            endYear = entity.endYear,
            season = entity.season?.let { MediaSeason.valueOf(it) },
            episodesChaptersRange = entity.episodesChaptersRange,
            durationVolumesRange = entity.durationVolumesRange,
            onMyList = entity.onMyList,
            isDoujin = entity.isDoujin,
            isAdult = entity.isAdult,
            country = entity.country?.let { CountryOfOrigin.valueOf(it) },
            sources = entity.sources?.map { MediaSource.valueOf(it) },
        )
    }
}
