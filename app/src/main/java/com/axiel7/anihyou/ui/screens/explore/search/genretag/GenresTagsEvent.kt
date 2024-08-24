package com.axiel7.anihyou.ui.screens.explore.search.genretag

import com.axiel7.anihyou.data.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.data.model.genre.SelectableGenre

interface GenresTagsEvent {
    fun setExternalGenre(value: SelectableGenre)
    fun setExternalTag(value: SelectableGenre)
    fun onFilterChanged(value: String)
    fun onGenreUpdated(value: SelectableGenre)
    suspend fun onGenreRemoved(name: String): GenresAndTagsForSearch
    fun onTagUpdated(value: SelectableGenre)
    suspend fun onTagRemoved(name: String): GenresAndTagsForSearch
    fun unselectAllGenresAndTags()
    suspend fun onDismissSheet()
    fun fetchGenreTagCollection()
}