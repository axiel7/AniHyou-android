package com.axiel7.anihyou.ui.screens.explore.search.genretag

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.data.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.data.model.genre.SelectableGenre
import com.axiel7.anihyou.ui.common.state.UiState

@Stable
data class GenresTagsUiState(
    val filter: String = "",
    val genres: SnapshotStateList<SelectableGenre> = mutableStateListOf(),
    val tags: SnapshotStateList<SelectableGenre> = mutableStateListOf(),
    val externalGenre: SelectableGenre? = null,
    val externalTag: SelectableGenre? = null,
    override val error: String? = null,
    override val isLoading: Boolean = true
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)

    val displayGenres by derivedStateOf {
        if (filter.isNotBlank())
            genres.filter { it.name.contains(filter, ignoreCase = true) }
        else genres
    }

    val displayTags by derivedStateOf {
        if (filter.isNotBlank())
            tags.filter { it.name.contains(filter, ignoreCase = true) }
        else tags
    }

    private fun selectedGenres() =
        genres.filter { it.state == SelectableGenre.State.SELECTED }.map { it.name }

    private fun excludedGenres() =
        genres.filter { it.state == SelectableGenre.State.EXCLUDED }.map { it.name }

    private fun selectedTags() =
        tags.filter { it.state == SelectableGenre.State.SELECTED }.map { it.name }

    private fun excludedTags() =
        tags.filter { it.state == SelectableGenre.State.EXCLUDED }.map { it.name }

    fun genresAndTagsForSearch() = GenresAndTagsForSearch(
        genreIn = selectedGenres(),
        genreNot = excludedGenres(),
        tagIn = selectedTags(),
        tagNot = excludedTags()
    )
}
