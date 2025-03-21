package com.axiel7.anihyou.core.ui.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.model.CurrentListType
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.model.media.ChartType

@Immutable
class NavActionManager(
    private val navController: NavHostController
) {
    fun goBack() {
        navController.popBackStack()
    }

    fun toMediaDetails(id: Int) {
        navController.navigate(Routes.MediaDetails(id))
    }

    fun toMediaActivity(mediaId: Int) {
        navController.navigate(Routes.MediaActivity(mediaId))
    }

    fun toCharacterDetails(id: Int) {
        navController.navigate(Routes.CharacterDetails(id))
    }

    fun toStaffDetails(id: Int) {
        navController.navigate(Routes.StaffDetails(id))
    }

    fun toStudioDetails(id: Int) {
        navController.navigate(Routes.StudioDetails(id))
    }

    fun toUserDetails(id: Int) {
        navController.navigate(Routes.UserDetails(id = id, userName = null))
    }

    fun toUserDetails(userId: Int?, username: String?) {
        navController.navigate(Routes.UserDetails(userId, username))
    }

    fun toActivityDetails(id: Int) {
        navController.navigate(Routes.ActivityDetails(id))
    }

    fun toThreadDetails(id: Int) {
        navController.navigate(Routes.ThreadDetails(id))
    }

    fun toReviewDetails(id: Int) {
        navController.navigate(Routes.ReviewDetails(id))
    }

    fun toFullscreenImage(url: String) {
        navController.navigate(Routes.FullScreenImage(url))
    }

    fun toSearch() {
        navController.navigate(Routes.Search(focus = true))
    }

    fun toSearchOnMyList(mediaType: MediaType) {
        navController.navigate(
            Routes.Search(mediaType = mediaType.rawValue, onList = true, focus = true)
        )
    }

    fun toGenreTag(
        mediaType: MediaType,
        genre: String?,
        tag: String?
    ) {
        navController.navigate(
            Routes.Search(mediaType = mediaType.rawValue, genre = genre, tag = tag)
        )
    }

    fun toAnimeSeason(season: AnimeSeason) {
        navController.navigate(
            Routes.SeasonAnime(season = season.season.rawValue, year = season.year)
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
        navController.navigate(Routes.Calendar)
    }

    fun toCurrentFullList(listType: CurrentListType) {
        navController.navigate(Routes.CurrentFullList(listType = listType))
    }

    fun toExplore(mediaType: MediaType, mediaSort: MediaSort) {
        navController.navigate(
            Routes.Search(mediaType = mediaType.rawValue, mediaSort = mediaSort.rawValue)
        )
    }

    fun toNotifications(unread: Int = 0) {
        navController.navigate(Routes.Notifications(unread))
    }

    fun toPublishNewActivity() {
        navController.navigate(
            Routes.PublishActivity(activityId = null, id = null, text = null)
        )
    }

    fun toPublishActivityReply(
        activityId: Int,
        replyId: Int?,
        text: String?
    ) {
        navController.navigate(
            Routes.PublishActivity(activityId = activityId, id = replyId, text = text)
        )
    }

    fun toPublishThreadComment(
        threadId: Int,
        commentId: Int?,
        text: String?
    ) {
        navController.navigate(
            Routes.PublishComment(threadId = threadId, id = commentId ?: 0, text = text)
        )
    }

    fun toPublishCommentReply(
        threadId: Int,
        parentCommentId: Int,
        commentId: Int?,
        text: String?
    ) {
        navController.navigate(
            Routes.PublishComment(
                threadId = threadId,
                parentCommentId = parentCommentId,
                id = commentId ?: 0,
                text = text
            )
        )
    }

    fun toMediaChart(type: ChartType) {
        navController.navigate(Routes.MediaChartList(type.name))
    }

    fun toUserMediaList(
        mediaType: MediaType,
        userId: Int,
        scoreFormat: ScoreFormat
    ) {
        navController.navigate(
            Routes.UserMediaList(
                mediaType = mediaType.rawValue,
                userId = userId,
                scoreFormat = scoreFormat.rawValue
            )
        )
    }

    fun toSettings() {
        navController.navigate(Routes.Settings)
    }

    fun toListStyleSettings() {
        navController.navigate(Routes.ListStyleSettings)
    }

    fun toCustomLists() {
        navController.navigate(Routes.CustomLists)
    }

    fun toTranslations() {
        navController.navigate(Routes.Translations)
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