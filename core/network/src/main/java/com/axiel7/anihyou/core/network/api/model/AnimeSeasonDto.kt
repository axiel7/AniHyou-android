package com.axiel7.anihyou.core.network.api.model

import com.axiel7.anihyou.core.network.type.MediaSeason
import kotlinx.serialization.Serializable

@Serializable
data class AnimeSeasonDto(
    val year: Int,
    val season: MediaSeason
)
