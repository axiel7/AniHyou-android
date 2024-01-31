package com.axiel7.anihyou.ui.screens.explore.search.genretag

import com.axiel7.anihyou.data.model.genre.SelectableGenre

interface GenresTagsEvent {
    fun setExternalGenre(value: SelectableGenre?)
    fun setExternalTag(value: SelectableGenre?)
    fun onFilterChanged(value: String)
    fun onGenreUpdated(value: SelectableGenre)
    fun onTagUpdated(value: SelectableGenre)
    fun unselectAllGenresAndTags()
}