package com.axiel7.anihyou.core.model.media

import com.axiel7.anihyou.core.network.api.model.AnimeThemesDto

data class AnimeThemes(
    val openingThemes: List<Theme>? = null,
    val endingThemes: List<Theme>? = null,
) {
    data class Theme(
        val id: Int,
        val animeId: Int,
        val text: String
    ) {
        fun toDto() = AnimeThemesDto.Theme(id, animeId, text)
    }

    fun toDto() = AnimeThemesDto(
        openingThemes = openingThemes?.map { it.toDto() },
        endingThemes = endingThemes?.map { it.toDto() },
    )

    companion object {
        fun AnimeThemesDto.Theme.toBo() = Theme(id, animeId, text)

        fun AnimeThemesDto.toBo() = AnimeThemes(
            openingThemes = openingThemes?.map { it.toBo() },
            endingThemes = endingThemes?.map { it.toBo() },
        )
    }
}
