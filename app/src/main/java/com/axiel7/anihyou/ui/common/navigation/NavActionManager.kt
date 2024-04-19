package com.axiel7.anihyou.ui.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.utils.NumberUtils.toStringOrZero
import com.axiel7.anihyou.utils.UTF_8
import java.net.URLEncoder

@Immutable
class NavActionManager(
    private val navController: NavHostController
) {
    fun goBack() {
        navController.popBackStack()
    }

    fun toMediaDetails(id: Int) {
        navController.navigate(
            NavDestination.MediaDetails
                .putArguments(mapOf(NavArgument.MediaId to id.toString()))
        )
    }

    fun toCharacterDetails(id: Int) {
        navController.navigate(
            NavDestination.CharacterDetails
                .putArguments(mapOf(NavArgument.CharacterId to id.toString()))
        )
    }

    fun toStaffDetails(id: Int) {
        navController.navigate(
            NavDestination.StaffDetails
                .putArguments(mapOf(NavArgument.StaffId to id.toString()))
        )
    }

    fun toStudioDetails(id: Int) {
        navController.navigate(
            NavDestination.StudioDetails
                .putArguments(mapOf(NavArgument.StudioId to id.toString()))
        )
    }

    fun toUserDetails(id: Int) {
        navController.navigate(
            NavDestination.UserDetails
                .putArguments(mapOf(NavArgument.UserId to id.toString()))
        )
    }

    fun toUserDetails(userId: Int?, username: String?) {
        navController.navigate(
            NavDestination.UserDetails.putArguments(
                mapOf(
                    NavArgument.UserId to (userId ?: 0).toString(),
                    NavArgument.UserName to username
                )
            )
        )
    }

    fun toActivityDetails(id: Int) {
        navController.navigate(
            NavDestination.ActivityDetails
                .putArguments(mapOf(NavArgument.ActivityId to id.toString()))
        )
    }

    fun toThreadDetails(id: Int) {
        navController.navigate(
            NavDestination.ThreadDetails.putArguments(
                mapOf(NavArgument.ThreadId to id.toString())
            )
        )
    }

    fun toReviewDetails(id: Int) {
        navController.navigate(
            NavDestination.ReviewDetails.putArguments(
                mapOf(NavArgument.ReviewId to id.toString())
            )
        )
    }

    fun toFullscreenImage(url: String) {
        val encodedUrl = URLEncoder.encode(url, UTF_8)
        navController.navigate(
            NavDestination.FullscreenImage
                .putArguments(mapOf(NavArgument.Url to encodedUrl))
        )
    }

    fun toSearch() {
        navController.navigate(NavDestination.Search.route())
    }

    fun toSearchOnMyList(mediaType: MediaType) {
        navController.navigate(
            NavDestination.Search.putArguments(
                mapOf(
                    NavArgument.MediaType to mediaType.rawValue,
                    NavArgument.OnList to TriBoolean.TRUE.value.toString(),
                    NavArgument.Focus to TriBoolean.TRUE.value.toString()
                )
            )
        )
    }

    fun toGenreTag(
        mediaType: MediaType,
        genre: String?,
        tag: String?
    ) {
        navController.navigate(
            NavDestination.Search.putArguments(
                mapOf(
                    NavArgument.MediaType to mediaType.rawValue,
                    NavArgument.Genre to genre,
                    NavArgument.Tag to tag
                )
            )
        )
    }

    fun toAnimeSeason(season: AnimeSeason) {
        navController.navigate(
            NavDestination.SeasonAnime.putArguments(
                mapOf(
                    NavArgument.Year to season.year.toString(),
                    NavArgument.Season to season.season.name
                )
            )
        )
    }

    fun toAnimeSeason(year: Int, season: MediaSeason) {
        toAnimeSeason(
            season = AnimeSeason(
                year = year,
                season = season,
            )
        )
    }

    fun toCalendar() {
        navController.navigate(NavDestination.Calendar.route())
    }

    fun toExplore(mediaType: MediaType, mediaSort: MediaSort) {
        navController.navigate(
            NavDestination.Search.putArguments(
                mapOf(
                    NavArgument.MediaType to mediaType.rawValue,
                    NavArgument.MediaSort to mediaSort.rawValue
                )
            )
        )
    }

    fun toNotifications(unread: Int = 0) {
        navController.navigate(
            NavDestination.Notifications
                .putArguments(mapOf(NavArgument.UnreadCount to unread.toString()))
        )
    }

    fun toPublishActivity(id: Int?, text: String?) {
        navController.navigate(
            NavDestination.PublishActivity.putArguments(
                mapOf(
                    NavArgument.ActivityId to id.toStringOrZero(),
                    NavArgument.Text to text
                )
            )
        )
    }

    fun toPublishActivityReply(
        activityId: Int,
        replyId: Int?,
        text: String?
    ) {
        navController.navigate(
            NavDestination.PublishActivityReply.putArguments(
                mapOf(
                    NavArgument.ActivityId to activityId.toString(),
                    NavArgument.ReplyId to replyId.toStringOrZero(),
                    NavArgument.Text to text
                )
            )
        )
    }

    fun toPublishThreadComment(
        threadId: Int,
        commentId: Int?,
        text: String?
    ) {
        navController.navigate(
            NavDestination.PublishThreadComment.putArguments(
                mapOf(
                    NavArgument.ThreadId to threadId.toString(),
                    NavArgument.CommentId to commentId.toStringOrZero(),
                    NavArgument.Text to text
                )
            )
        )
    }

    fun toPublishCommentReply(
        parentCommentId: Int,
        commentId: Int?,
        text: String?
    ) {
        navController.navigate(
            NavDestination.PublishCommentReply.putArguments(
                mapOf(
                    NavArgument.ParentCommentId to parentCommentId.toString(),
                    NavArgument.CommentId to commentId.toStringOrZero(),
                    NavArgument.Text to text
                )
            )
        )
    }

    fun toMediaChart(type: ChartType) {
        navController.navigate(
            NavDestination.MediaChart.putArguments(
                mapOf(NavArgument.ChartType to type.name)
            )
        )
    }

    fun toUserMediaList(
        mediaType: MediaType,
        userId: Int,
        scoreFormat: ScoreFormat
    ) {
        navController.navigate(
            NavDestination.UserMediaList.putArguments(
                mapOf(
                    NavArgument.UserId to userId.toString(),
                    NavArgument.MediaType to mediaType.rawValue,
                    NavArgument.ScoreFormat to scoreFormat.rawValue
                )
            )
        )
    }

    fun toSettings() {
        navController.navigate(NavDestination.Settings.route())
    }

    fun toListStyleSettings() {
        navController.navigate(NavDestination.ListStyleSettings.route())
    }

    fun toTranslations() {
        navController.navigate(NavDestination.Translations.route())
    }

    companion object {
        @Composable
        fun rememberNavActionManager(
            navController: NavHostController = rememberNavController()
        ) = remember {
            NavActionManager(navController)
        }
    }
}