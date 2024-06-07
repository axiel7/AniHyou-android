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
import com.axiel7.anihyou.ui.composables.FullScreenImage
import com.axiel7.anihyou.ui.screens.activitydetails.ActivityDetails
import com.axiel7.anihyou.ui.screens.activitydetails.publish.PublishActivity
import com.axiel7.anihyou.ui.screens.calendar.Calendar
import com.axiel7.anihyou.ui.screens.characterdetails.CharacterDetails
import com.axiel7.anihyou.ui.screens.explore.charts.MediaChartList
import com.axiel7.anihyou.ui.screens.explore.search.Search
import com.axiel7.anihyou.ui.screens.explore.season.SeasonAnime
import com.axiel7.anihyou.ui.screens.mediadetails.MediaDetails
import com.axiel7.anihyou.ui.screens.notifications.Notifications
import com.axiel7.anihyou.ui.screens.profile.UserDetails
import com.axiel7.anihyou.ui.screens.reviewdetails.ReviewDetails
import com.axiel7.anihyou.ui.screens.settings.Settings
import com.axiel7.anihyou.ui.screens.settings.Translations
import com.axiel7.anihyou.ui.screens.settings.liststyle.ListStyleSettings
import com.axiel7.anihyou.ui.screens.staffdetails.StaffDetails
import com.axiel7.anihyou.ui.screens.studiodetails.StudioDetails
import com.axiel7.anihyou.ui.screens.thread.ThreadDetails
import com.axiel7.anihyou.ui.screens.thread.publish.PublishComment
import com.axiel7.anihyou.ui.screens.usermedialist.UserMediaList

@Immutable
class NavActionManager(
    private val navController: NavHostController
) {
    fun goBack() {
        navController.popBackStack()
    }

    fun toMediaDetails(id: Int) {
        navController.navigate(MediaDetails(id))
    }

    fun toCharacterDetails(id: Int) {
        navController.navigate(CharacterDetails(id))
    }

    fun toStaffDetails(id: Int) {
        navController.navigate(StaffDetails(id))
    }

    fun toStudioDetails(id: Int) {
        navController.navigate(StudioDetails(id))
    }

    fun toUserDetails(id: Int) {
        navController.navigate(UserDetails(id = id, userName = null))
    }

    fun toUserDetails(userId: Int?, username: String?) {
        navController.navigate(UserDetails(userId ?: 0, username))
    }

    fun toActivityDetails(id: Int) {
        navController.navigate(ActivityDetails(id))
    }

    fun toThreadDetails(id: Int) {
        navController.navigate(ThreadDetails(id))
    }

    fun toReviewDetails(id: Int) {
        navController.navigate(ReviewDetails(id))
    }

    fun toFullscreenImage(url: String) {
        navController.navigate(FullScreenImage(url))
    }

    fun toSearch() {
        navController.navigate(Search(focus = true))
    }

    fun toSearchOnMyList(mediaType: MediaType) {
        navController.navigate(
            Search(mediaType = mediaType.rawValue, onList = TriBoolean.TRUE.value, focus = true)
        )
    }

    fun toGenreTag(
        mediaType: MediaType,
        genre: String?,
        tag: String?
    ) {
        navController.navigate(
            Search(mediaType = mediaType.rawValue, genre = genre, tag = tag)
        )
    }

    fun toAnimeSeason(season: AnimeSeason) {
        navController.navigate(
            SeasonAnime(season = season.season.rawValue, year = season.year)
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
        navController.navigate(Calendar)
    }

    fun toExplore(mediaType: MediaType, mediaSort: MediaSort) {
        navController.navigate(
            Search(mediaType = mediaType.rawValue, mediaSort = mediaSort.rawValue)
        )
    }

    fun toNotifications(unread: Int = 0) {
        navController.navigate(Notifications(unread))
    }

    fun toPublishActivity(id: Int?, text: String?) {
        navController.navigate(
            PublishActivity(activityId = id ?: 0, text = text)
        )
    }

    fun toPublishActivityReply(
        activityId: Int,
        replyId: Int?,
        text: String?
    ) {
        navController.navigate(
            PublishActivity(activityId = activityId, id = replyId ?: 0, text = text)
        )
    }

    fun toPublishThreadComment(
        threadId: Int,
        commentId: Int?,
        text: String?
    ) {
        navController.navigate(
            PublishComment(threadId = threadId, id = commentId ?: 0, text = text)
        )
    }

    fun toPublishCommentReply(
        parentCommentId: Int,
        commentId: Int?,
        text: String?
    ) {
        navController.navigate(
            PublishComment(
                parentCommentId = parentCommentId,
                id = commentId ?: 0,
                text = text
            )
        )
    }

    fun toMediaChart(type: ChartType) {
        navController.navigate(MediaChartList(type.name))
    }

    fun toUserMediaList(
        mediaType: MediaType,
        userId: Int,
        scoreFormat: ScoreFormat
    ) {
        navController.navigate(
            UserMediaList(
                mediaType = mediaType.rawValue,
                userId = userId,
                scoreFormat = scoreFormat.rawValue
            )
        )
    }

    fun toSettings() {
        navController.navigate(Settings)
    }

    fun toListStyleSettings() {
        navController.navigate(ListStyleSettings)
    }

    fun toTranslations() {
        navController.navigate(Translations)
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