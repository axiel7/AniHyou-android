package com.axiel7.anihyou.feature.explore.search

import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.model.SearchType
import com.axiel7.anihyou.core.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.core.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.core.network.SearchMediaQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.type.MediaSort

interface SearchEvent : PagedEvent {
    fun setQuery(value: String)
    fun setSearchType(value: SearchType)
    fun setMediaSort(value: MediaSort)
    fun setMediaFormats(values: List<MediaFormatLocalizable>)
    fun setMediaStatuses(values: List<MediaStatusLocalizable>)
    fun setStartYear(value: Int?)
    fun setEndYear(value: Int?)
    fun setSeason(value: MediaSeason?)
    fun setEpCh(value: IntRange?)
    fun setDuration(value: IntRange?)
    fun setOnMyList(value: Boolean?)
    fun setIsDoujin(value: Boolean?)
    fun setIsAdult(value: Boolean?)
    fun setCountry(value: CountryOfOrigin?)
    fun onGenreTagStateChanged(genresAndTagsForSearch: GenresAndTagsForSearch)
    fun clearFilters()
    fun selectMediaItem(value: SearchMediaQuery.Medium?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}