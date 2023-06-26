package com.axiel7.anihyou.data.model

data class DeepLink(
    val type: Type,
    val id: String,
) {
    enum class Type {
        ANIME, MANGA, CHARACTER, STAFF, STUDIO, USER
    }
}
