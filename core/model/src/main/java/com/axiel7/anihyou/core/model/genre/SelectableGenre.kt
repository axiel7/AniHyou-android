package com.axiel7.anihyou.core.model.genre

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

data class SelectableGenre(
    val name: String,
    val state: State = State.NONE,
) : Localizable {

    enum class State {
        NONE, SELECTED, EXCLUDED;

        val toggleableState
            get() = when (this) {
                NONE -> ToggleableState.Off
                SELECTED -> ToggleableState.On
                EXCLUDED -> ToggleableState.Indeterminate
            }
    }

    fun setState(toggleableState: ToggleableState) =
        copy(
            state = when (toggleableState) {
                ToggleableState.On -> State.SELECTED
                ToggleableState.Off -> State.NONE
                ToggleableState.Indeterminate -> State.EXCLUDED
            }
        )

    @Composable
    override fun localized() = name.genreTagLocalized()

    companion object {
        @Composable
        fun String.genreTagLocalized() = genreTagStringRes()?.let { stringResource(it) } ?: this

        @StringRes
        fun String.genreTagStringRes() = when (this) {
            "Action" -> R.string.genre_action
            "Adventure" -> R.string.genre_adventure
            "Comedy" -> R.string.genre_comedy
            "Drama" -> R.string.genre_drama
            "Ecchi" -> R.string.genre_ecchi
            "Fantasy" -> R.string.genre_fantasy
            "Hentai" -> R.string.genre_hentai
            "Horror" -> R.string.genre_horror
            "Mahou Shoujo" -> R.string.genre_mahou_shoujo
            "Mecha" -> R.string.genre_mecha
            "Music" -> R.string.genre_music
            "Mystery" -> R.string.genre_mystery
            "Psychological" -> R.string.genre_pyshological
            "Romance" -> R.string.genre_romance
            "Sci-Fi" -> R.string.genre_scifi
            "Slice of Life" -> R.string.genre_slice_of_life
            "Sports" -> R.string.genre_sports
            "Supernatural" -> R.string.genre_supernatural
            "Thriller" -> R.string.genre_thriller
            else -> null
        }
    }
}