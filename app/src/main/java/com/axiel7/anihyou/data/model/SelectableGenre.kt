package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

data class SelectableGenre(
    val name: String,
    val isSelected: Boolean = false,
) : Localizable {

    @Composable
    override fun localized() = name.genreTagLocalized()

    companion object {
        @Composable
        fun String.genreTagLocalized() = when (this) {
            "Action" -> stringResource(R.string.genre_action)
            "Adventure" -> stringResource(R.string.genre_adventure)
            "Comedy" -> stringResource(R.string.genre_comedy)
            "Drama" -> stringResource(R.string.genre_drama)
            "Ecchi" -> stringResource(R.string.genre_ecchi)
            "Fantasy" -> stringResource(R.string.genre_fantasy)
            "Hentai" -> stringResource(R.string.genre_hentai)
            "Horror" -> stringResource(R.string.genre_horror)
            "Mahou Shoujo" -> stringResource(R.string.genre_mahou_shoujo)
            "Mecha" -> stringResource(R.string.genre_mecha)
            "Music" -> stringResource(R.string.genre_music)
            "Mystery" -> stringResource(R.string.genre_mystery)
            "Psychological" -> stringResource(R.string.genre_pyshological)
            "Romance" -> stringResource(R.string.genre_romance)
            "Sci-Fi" -> stringResource(R.string.genre_scifi)
            "Slice of Life" -> stringResource(R.string.genre_slice_of_life)
            "Sports" -> stringResource(R.string.genre_sports)
            "Supernatural" -> stringResource(R.string.genre_supernatural)
            "Thriller" -> stringResource(R.string.genre_thriller)
            else -> this
        }
    }
}