package com.axiel7.anihyou.feature.explore.search.genretag

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.core.model.genre.SelectableGenre

@Immutable
interface GenresTagsEvent {
    fun setExternalGenre(value: SelectableGenre)
    fun setExternalTag(value: SelectableGenre)
    fun onFilterChanged(value: String)
    fun onGenreUpdated(value: SelectableGenre)
    suspend fun onGenreRemoved(name: String): GenresAndTagsForSearch
    fun onTagUpdated(value: SelectableGenre)
    suspend fun onTagRemoved(name: String): GenresAndTagsForSearch
    fun onMinTagPercentageUpdated(value: Int)
    fun unselectAllGenresAndTags()
    fun resetData()
    suspend fun onDismissSheet()
    fun fetchGenreTagCollection()
}