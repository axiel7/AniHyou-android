package com.axiel7.anihyou.data.model.genre

data class GenresAndTags(
    val genres: List<SelectableGenre>,
    val tags: List<SelectableGenre>,
)
