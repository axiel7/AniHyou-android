package com.axiel7.anihyou.ui.screens.explore.search

import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.data.model.media.CountryOfOrigin
import com.axiel7.anihyou.data.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.data.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.ui.common.event.PagedEvent

interface SearchEvent : PagedEvent {
    fun setQuery(value: String)
    fun setSearchType(value: SearchType)
    fun setMediaSort(value: MediaSort)
    fun setMediaFormats(values: List<MediaFormatLocalizable>)
    fun setMediaStatuses(values: List<MediaStatusLocalizable>)
    fun setStartYear(value: Int?)
    fun setEndYear(value: Int?)
    fun setSeason(value: MediaSeason?)
    fun setOnMyList(value: Boolean?)
    fun setIsDoujin(value: Boolean?)
    fun setIsAdult(value: Boolean?)
    fun setCountry(value: CountryOfOrigin?)
    fun onGenreTagStateChanged(genresAndTagsForSearch: GenresAndTagsForSearch)
    fun clearFilters()
    fun selectMediaItem(value: SearchMediaQuery.Medium?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}