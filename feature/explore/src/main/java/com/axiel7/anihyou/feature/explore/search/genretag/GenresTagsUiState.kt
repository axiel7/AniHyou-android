package com.axiel7.anihyou.feature.explore.search.genretag

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.common.extensions.indexOfFirstOrNull
import com.axiel7.anihyou.core.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.core.model.genre.SelectableGenre
import com.axiel7.anihyou.core.ui.common.state.UiState

@Stable
data class GenresTagsUiState(
    val filter: String = "",
    val genres: SnapshotStateList<SelectableGenre> = mutableStateListOf(),
    val tags: SnapshotStateList<SelectableGenre> = mutableStateListOf(),
    val displayGenres: SnapshotStateList<SelectableGenre> = mutableStateListOf(),
    val displayTags: SnapshotStateList<SelectableGenre> = mutableStateListOf(),
    val externalGenre: SelectableGenre? = null,
    val externalTag: SelectableGenre? = null,
    val genresAndTagsForSearch: GenresAndTagsForSearch = GenresAndTagsForSearch(),
    override val error: String? = null,
    override val isLoading: Boolean = true
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)

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

    fun updateGenre(value: SelectableGenre) {
        genres.indexOfFirstOrNull { it.name == value.name }?.let { index ->
            genres[index] = value
        }
        displayGenres.indexOfFirstOrNull { it.name == value.name }?.let { index ->
            displayGenres[index] = value
        }
    }

    fun updateTag(value: SelectableGenre) {
        tags.indexOfFirstOrNull { it.name == value.name }?.let { index ->
            tags[index] = value
        }
        displayTags.indexOfFirstOrNull { it.name == value.name }?.let { index ->
            displayTags[index] = value
        }
    }
}
