package com.axiel7.anihyou.core.ui.common.navigation

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.model.CurrentListType
import kotlinx.serialization.Serializable

object Routes {

    @Serializable
    @Immutable
    object Home

    @Serializable
    @Immutable
    object AnimeTab

    @Serializable
    @Immutable
    object MangaTab

    @Serializable
    @Immutable
    data class UserMediaList(
        val mediaType: String,
        val userId: Int = 0,
        val scoreFormat: String? = null,
    )

    @Serializable
    @Immutable
    object Profile

    @Serializable
    @Immutable
    data class UserDetails(
        val id: Int?,
        val userName: String?
    )

    @Serializable
    object Explore

    @Serializable
    @Immutable
    data class Search(
        val mediaType: String? = null,
        val mediaSort: String? = null,
        val genre: String? = null,
        val tag: String? = null,
        val onList: Boolean? = null,
        val focus: Boolean = false,
    )

    @Serializable
    @Immutable
    data class Notifications(val unreadCount: Int = 0)

    @Serializable
    @Immutable
    data class MediaDetails(val id: Int)

    @Serializable
    @Immutable
    data class MediaChartList(val type: String)

    @Serializable
    @Immutable
    data class SeasonAnime(
        val season: String,
        val year: Int,
    )

    @Serializable
    @Immutable
    object Calendar

    @Serializable
    @Immutable
    data class CharacterDetails(val id: Int)

    @Serializable
    @Immutable
    data class StaffDetails(val id: Int)

    @Serializable
    @Immutable
    data class ReviewDetails(val id: Int)

    @Serializable
    @Immutable
    data class ThreadDetails(val id: Int)

    @Serializable
    @Immutable
    data class StudioDetails(val id: Int)

    @Serializable
    @Immutable
    object Settings

    @Serializable
    @Immutable
    object ListStyleSettings

    @Serializable
    object CustomLists

    @Serializable
    @Immutable
    object Translations

    @Serializable
    @Immutable
    data class FullScreenImage(val imageUrl: String?)

    @Serializable
    @Immutable
    data class ActivityDetails(val id: Int)

    @Serializable
    @Immutable
    data class PublishActivity(
        val activityId: Int?,
        val id: Int?,
        val text: String? = null
    )

    @Serializable
    @Immutable
    data class PublishComment(
        val threadId: Int = 0,
        val parentCommentId: Int = 0,
        val id: Int = 0,
        val text: String? = null,
    )

    @Serializable
    @Immutable
    data class MediaActivity(
        val mediaId: Int
    )

    @Serializable
    @Immutable
    data class CurrentFullList(
        val listType: CurrentListType,
    )
}