package com.axiel7.anihyou.core.model

import androidx.compose.runtime.Stable

@Stable
data class DeepLink(
    val type: Type,
    val id: String,
) {
    // We could have ANIME and MANGA combined, but AniList urls have the distinction.
    // In the end both of them should open the MediaDetailsView
    enum class Type {
        ANIME, MANGA, CHARACTER, STAFF, STUDIO, USER, SEARCH, THREAD, ACTIVITY;

        val intentAction
            get() = when (this) {
                ANIME -> "media_details"
                MANGA -> "media_details"
                CHARACTER -> "character_details"
                STAFF -> "staff_details"
                STUDIO -> "studio_details"
                USER -> "user_details"
                SEARCH -> "search"
                THREAD -> "thread_details"
                ACTIVITY -> "activity_details"
            }
    }
}
