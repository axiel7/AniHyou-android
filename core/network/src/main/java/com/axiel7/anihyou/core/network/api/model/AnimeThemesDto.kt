package com.axiel7.anihyou.core.network.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeThemesDto(
    @SerialName("opening_themes")
    val openingThemes: List<Theme>? = null,
    @SerialName("ending_themes")
    val endingThemes: List<Theme>? = null,
) {
    @Serializable
    data class Theme(
        @SerialName("id")
        val id: Int,
        @SerialName("anime_id")
        val animeId: Int,
        @SerialName("text")
        val text: String
    )
}
