package com.axiel7.anihyou.core.model.genre

data class GenresAndTags(
    val genres: List<SelectableGenre>,
    val tags: List<SelectableGenre>,
)
