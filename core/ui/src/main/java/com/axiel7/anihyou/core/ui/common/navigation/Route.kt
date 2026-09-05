package com.axiel7.anihyou.core.ui.common.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import com.axiel7.anihyou.core.model.CurrentListType
import com.axiel7.anihyou.core.model.FavoritesType
import com.axiel7.anihyou.core.model.thread.ChildComment
import kotlinx.serialization.Serializable

@Immutable
sealed interface Route : NavKey {

    @Serializable
    @Immutable
    object Home: Route

    @Serializable
    @Immutable
    object AnimeTab: Route

    @Serializable
    @Immutable
    object MangaTab: Route

    @Serializable
    @Immutable
    data class UserMediaList(
        val mediaType: String,
        val userId: Int = 0,
        val scoreFormat: String? = null,
    ): Route

    @Serializable
    @Immutable
    object Profile: Route

    @Serializable
    @Immutable
    data class UserDetails(
        val id: Int?,
        val userName: String?
    ): Route

    @Serializable
    object Explore: Route

    @Serializable
    @Immutable
    data class Search(
        val mediaType: String? = null,
        val mediaSort: String? = null,
        val genre: String? = null,
        val tag: String? = null,
        val onList: Boolean? = null,
        val focus: Boolean = false,
    ): Route

    @Serializable
    @Immutable
    data class Notifications(val unreadCount: Int = 0): Route

    @Serializable
    @Immutable
    data class MediaDetails(
        val id: Int,
        val isLoggedIn: Boolean = false,
    ): Route

    @Serializable
    @Immutable
    data class MediaChartList(val type: String): Route

    @Serializable
    @Immutable
    data class SeasonAnime(
        val season: String,
        val year: Int,
    ): Route

    @Serializable
    @Immutable
    object Calendar: Route

    @Serializable
    @Immutable
    data class CharacterDetails(val id: Int): Route

    @Serializable
    @Immutable
    data class StaffDetails(val id: Int): Route

    @Serializable
    @Immutable
    data class ReviewDetails(val id: Int): Route

    @Serializable
    @Immutable
    data class ThreadDetails(val id: Int): Route

    @Serializable
    @Immutable
    data class ThreadCommentDetails(val childComment: ChildComment): Route

    @Serializable
    @Immutable
    data class StudioDetails(val id: Int): Route

    @Serializable
    @Immutable
    object Settings: Route

    @Serializable
    @Immutable
    object ListStyleSettings: Route

    @Serializable
    object CustomLists: Route

    @Serializable
    object CustomLinks: Route

    @Serializable
    @Immutable
    object Translations: Route

    @Serializable
    @Immutable
    object Contributors: Route

    @Serializable
    @Immutable
    data class FullScreenImage(val imageUrl: String?): Route

    @Serializable
    @Immutable
    data class ActivityDetails(val id: Int): Route

    @Serializable
    @Immutable
    data class PublishActivity(
        val activityId: Int?,
        val id: Int?,
        val text: String? = null
    ): Route

    @Serializable
    @Immutable
    data class PublishComment(
        val threadId: Int = 0,
        val parentCommentId: Int = 0,
        val id: Int = 0,
        val text: String? = null,
    ): Route

    @Serializable
    @Immutable
    data class MediaActivity(
        val mediaId: Int
    ): Route

    @Serializable
    @Immutable
    data class MediaCharacters(
        val mediaId: Int
    ): Route

    @Serializable
    @Immutable
    data class CurrentFullList(
        val listType: CurrentListType,
    ): Route

    @Serializable
    @Immutable
    data class ReorderFavorites(
        val userId: Int,
        val type: FavoritesType
    ) : Route

    @Serializable
    object PriorityColors : Route
}