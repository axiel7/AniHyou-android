package com.axiel7.anihyou.data.model.genre

data class GenresAndTagsForSearch(
    val genreIn: List<String> = emptyList(),
    val genreNot: List<String> = emptyList(),
    val tagIn: List<String> = emptyList(),
    val tagNot: List<String> = emptyList(),
)
