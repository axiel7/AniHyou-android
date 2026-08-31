package com.axiel7.anihyou.core.model.genre

data class GenresAndTagsForSearch(
    val genreIn: Set<String> = emptySet(),
    val genreNot: Set<String> = emptySet(),
    val tagIn: Set<String> = emptySet(),
    val tagNot: Set<String> = emptySet(),
    val minimumTagPercentage: Int = 18,
) {
    val totalSize get() = genreIn.size + genreNot.size + tagIn.size + tagNot.size
}
