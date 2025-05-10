package com.axiel7.anihyou.feature.explore.search.genretag

import com.axiel7.anihyou.core.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.core.model.genre.SelectableGenre

interface GenresTagsEvent {
    fun setExternalGenre(value: SelectableGenre)
    fun setExternalTag(value: SelectableGenre)
    fun onFilterChanged(value: String)
    fun onGenreUpdated(value: SelectableGenre)
    suspend fun onGenreRemoved(name: String): GenresAndTagsForSearch
    fun onTagUpdated(value: SelectableGenre)
    suspend fun onTagRemoved(name: String): GenresAndTagsForSearch
    fun unselectAllGenresAndTags()
    fun resetData()
    suspend fun onDismissSheet()
    fun fetchGenreTagCollection()
}