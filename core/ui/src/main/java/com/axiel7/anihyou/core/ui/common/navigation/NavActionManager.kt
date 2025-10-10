package com.axiel7.anihyou.core.ui.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.axiel7.anihyou.core.model.CurrentListType
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.model.media.ChartType
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.ui.common.BottomDestination

@Immutable
class NavActionManager(
    private val backStack: TopLevelBackStack<NavKey>
) {
    fun goBack() {
        backStack.removeLast()
    }

    fun toMediaDetails(id: Int) {
        backStack.add(Routes.MediaDetails(id))
    }

    fun toMediaActivity(mediaId: Int) {
        backStack.add(Routes.MediaActivity(mediaId))
    }

    fun toCharacterDetails(id: Int) {
        backStack.add(Routes.CharacterDetails(id))
    }

    fun toStaffDetails(id: Int) {
        backStack.add(Routes.StaffDetails(id))
    }

    fun toStudioDetails(id: Int) {
        backStack.add(Routes.StudioDetails(id))
    }

    fun toUserDetails(id: Int) {
        backStack.add(Routes.UserDetails(id = id, userName = null))
    }

    fun toUserDetails(userId: Int?, username: String?) {
        backStack.add(Routes.UserDetails(userId, username))
    }

    fun toActivityDetails(id: Int) {
        backStack.add(Routes.ActivityDetails(id))
    }

    fun toThreadDetails(id: Int) {
        backStack.add(Routes.ThreadDetails(id))
    }

    fun toReviewDetails(id: Int) {
        backStack.add(Routes.ReviewDetails(id))
    }

    fun toFullscreenImage(url: String) {
        backStack.add(Routes.FullScreenImage(url))
    }

    fun toSearch() {
        backStack.add(Routes.Search(focus = true))
    }

    fun toSearchOnMyList(mediaType: MediaType) {
        backStack.add(
            Routes.Search(mediaType = mediaType.rawValue, onList = true, focus = true)
        )
    }

    fun toGenreTag(
        mediaType: MediaType,
        genre: String?,
        tag: String?
    ) {
        backStack.add(
            Routes.Search(mediaType = mediaType.rawValue, genre = genre, tag = tag)
        )
    }

    fun toAnimeSeason(season: AnimeSeason) {
        backStack.add(
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
        backStack.add(Routes.Calendar)
    }

    fun toCurrentFullList(listType: CurrentListType) {
        backStack.add(Routes.CurrentFullList(listType = listType))
    }

    fun toExplore(mediaType: MediaType, mediaSort: MediaSort) {
        backStack.add(
            Routes.Search(mediaType = mediaType.rawValue, mediaSort = mediaSort.rawValue)
        )
    }

    fun toNotifications(unread: Int = 0) {
        backStack.add(Routes.Notifications(unread))
    }

    fun toPublishNewActivity() {
        backStack.add(
            Routes.PublishActivity(activityId = null, id = null, text = null)
        )
    }

    fun toPublishActivityReply(
        activityId: Int,
        replyId: Int?,
        text: String?
    ) {
        backStack.add(
            Routes.PublishActivity(activityId = activityId, id = replyId, text = text)
        )
    }

    fun toPublishThreadComment(
        threadId: Int,
        commentId: Int?,
        text: String?
    ) {
        backStack.add(
            Routes.PublishComment(threadId = threadId, id = commentId ?: 0, text = text)
        )
    }

    fun toPublishCommentReply(
        threadId: Int,
        parentCommentId: Int,
        commentId: Int?,
        text: String?
    ) {
        backStack.add(
            Routes.PublishComment(
                threadId = threadId,
                parentCommentId = parentCommentId,
                id = commentId ?: 0,
                text = text
            )
        )
    }

    fun toMediaChart(type: ChartType) {
        backStack.add(Routes.MediaChartList(type.name))
    }

    fun toUserMediaList(
        mediaType: MediaType,
        userId: Int,
        scoreFormat: ScoreFormat
    ) {
        backStack.add(
            Routes.UserMediaList(
                mediaType = mediaType.rawValue,
                userId = userId,
                scoreFormat = scoreFormat.rawValue
            )
        )
    }

    fun toSettings() {
        backStack.add(Routes.Settings)
    }

    fun toListStyleSettings() {
        backStack.add(Routes.ListStyleSettings)
    }

    fun toCustomLists() {
        backStack.add(Routes.CustomLists)
    }

    fun toTranslations() {
        backStack.add(Routes.Translations)
    }

    companion object {
        @Composable
        fun rememberNavActionManager(
            backStack: TopLevelBackStack<NavKey> = TopLevelBackStack(
                startKey = BottomDestination.Home.route,
                backStack = rememberNavBackStack()
            )
        ) = remember {
            NavActionManager(backStack)
        }
    }
}