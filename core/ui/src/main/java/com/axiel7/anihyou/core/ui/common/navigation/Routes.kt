package com.axiel7.anihyou.core.ui.common.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import com.axiel7.anihyou.core.model.CurrentListType
import kotlinx.serialization.Serializable

object Routes {

    @Serializable
    @Immutable
    object Home: NavKey

    @Serializable
    @Immutable
    object AnimeTab: NavKey

    @Serializable
    @Immutable
    object MangaTab: NavKey

    @Serializable
    @Immutable
    data class UserMediaList(
        val mediaType: String,
        val userId: Int = 0,
        val scoreFormat: String? = null,
        val isCompactScreen: Boolean = true,
    ): NavKey

    @Serializable
    @Immutable
    object Profile: NavKey

    @Serializable
    @Immutable
    data class UserDetails(
        val id: Int?,
        val userName: String?
    ): NavKey

    @Serializable
    object Explore: NavKey

    @Serializable
    @Immutable
    data class Search(
        val mediaType: String? = null,
        val mediaSort: String? = null,
        val genre: String? = null,
        val tag: String? = null,
        val onList: Boolean? = null,
        val focus: Boolean = false,
    ): NavKey

    @Serializable
    @Immutable
    data class Notifications(val unreadCount: Int = 0): NavKey

    @Serializable
    @Immutable
    data class MediaDetails(
        val id: Int,
        val isLoggedIn: Boolean = false,
    ): NavKey

    @Serializable
    @Immutable
    data class MediaChartList(val type: String): NavKey

    @Serializable
    @Immutable
    data class SeasonAnime(
        val season: String,
        val year: Int,
    ): NavKey

    @Serializable
    @Immutable
    object Calendar: NavKey

    @Serializable
    @Immutable
    data class CharacterDetails(val id: Int): NavKey

    @Serializable
    @Immutable
    data class StaffDetails(val id: Int): NavKey

    @Serializable
    @Immutable
    data class ReviewDetails(val id: Int): NavKey

    @Serializable
    @Immutable
    data class ThreadDetails(val id: Int): NavKey

    @Serializable
    @Immutable
    data class StudioDetails(val id: Int): NavKey

    @Serializable
    @Immutable
    object Settings: NavKey

    @Serializable
    @Immutable
    object ListStyleSettings: NavKey

    @Serializable
    object CustomLists: NavKey

    @Serializable
    @Immutable
    object Translations: NavKey

    @Serializable
    @Immutable
    data class FullScreenImage(val imageUrl: String?): NavKey

    @Serializable
    @Immutable
    data class ActivityDetails(val id: Int): NavKey

    @Serializable
    @Immutable
    data class PublishActivity(
        val activityId: Int?,
        val id: Int?,
        val text: String? = null
    ): NavKey

    @Serializable
    @Immutable
    data class PublishComment(
        val threadId: Int = 0,
        val parentCommentId: Int = 0,
        val id: Int = 0,
        val text: String? = null,
    ): NavKey

    @Serializable
    @Immutable
    data class MediaActivity(
        val mediaId: Int
    ): NavKey

    @Serializable
    @Immutable
    data class CurrentFullList(
        val listType: CurrentListType,
    ): NavKey
}