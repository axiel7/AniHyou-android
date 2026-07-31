package com.axiel7.anihyou.core.ui.common.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.axiel7.anihyou.core.model.CurrentListType
import com.axiel7.anihyou.core.model.FavoritesType
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.model.media.ChartType
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat

@Immutable
class NavActionManager(
    private val navigator: INavigator,
) {
    fun goBack() {
        navigator.goBack()
    }

    fun toMediaDetails(id: Int) {
        navigator.navigate(Routes.MediaDetails(id))
    }

    fun toMediaActivity(mediaId: Int) {
        navigator.navigate(Routes.MediaActivity(mediaId))
    }

    fun toCharacterDetails(id: Int) {
        navigator.navigate(Routes.CharacterDetails(id))
    }

    fun toStaffDetails(id: Int) {
        navigator.navigate(Routes.StaffDetails(id))
    }

    fun toStudioDetails(id: Int) {
        navigator.navigate(Routes.StudioDetails(id))
    }

    fun toUserDetails(id: Int) {
        navigator.navigate(Routes.UserDetails(id = id, userName = null))
    }

    fun toUserDetails(userId: Int?, username: String?) {
        navigator.navigate(Routes.UserDetails(userId, username))
    }

    fun toActivityDetails(id: Int) {
        navigator.navigate(Routes.ActivityDetails(id))
    }

    fun toThreadDetails(id: Int) {
        navigator.navigate(Routes.ThreadDetails(id))
    }

    fun toReviewDetails(id: Int) {
        navigator.navigate(Routes.ReviewDetails(id))
    }

    fun toFullscreenImage(url: String) {
        navigator.navigate(Routes.FullScreenImage(url))
    }

    fun toSearch() {
        navigator.navigate(Routes.Search(focus = true))
    }

    fun toSearchOnMyList(mediaType: MediaType) {
        navigator.navigate(
            Routes.Search(mediaType = mediaType.rawValue, onList = true, focus = true)
        )
    }

    fun toGenreTag(
        mediaType: MediaType,
        genre: String?,
        tag: String?
    ) {
        navigator.navigate(
            Routes.Search(mediaType = mediaType.rawValue, genre = genre, tag = tag)
        )
    }

    fun toAnimeSeason(season: AnimeSeason) {
        navigator.navigate(
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
        navigator.navigate(Routes.Calendar)
    }

    fun toCurrentFullList(listType: CurrentListType) {
        navigator.navigate(Routes.CurrentFullList(listType = listType))
    }

    fun toExplore(mediaType: MediaType, mediaSort: MediaSort) {
        navigator.navigate(
            Routes.Search(mediaType = mediaType.rawValue, mediaSort = mediaSort.rawValue)
        )
    }

    fun toNotifications(unread: Int = 0) {
        navigator.navigate(Routes.Notifications(unread))
    }

    fun toPublishNewActivity() {
        navigator.navigate(
            Routes.PublishActivity(activityId = null, id = null, text = null)
        )
    }

    fun toPublishActivityReply(
        activityId: Int,
        replyId: Int?,
        text: String?
    ) {
        navigator.navigate(
            Routes.PublishActivity(activityId = activityId, id = replyId, text = text)
        )
    }

    fun toPublishThreadComment(
        threadId: Int,
        commentId: Int?,
        text: String?
    ) {
        navigator.navigate(
            Routes.PublishComment(threadId = threadId, id = commentId ?: 0, text = text)
        )
    }

    fun toPublishCommentReply(
        threadId: Int,
        parentCommentId: Int,
        commentId: Int?,
        text: String?
    ) {
        navigator.navigate(
            Routes.PublishComment(
                threadId = threadId,
                parentCommentId = parentCommentId,
                id = commentId ?: 0,
                text = text
            )
        )
    }

    fun toMediaChart(type: ChartType) {
        navigator.navigate(Routes.MediaChartList(type.name))
    }

    fun toUserMediaList(
        mediaType: MediaType,
        userId: Int,
        scoreFormat: ScoreFormat
    ) {
        navigator.navigate(
            Routes.UserMediaList(
                mediaType = mediaType.rawValue,
                userId = userId,
                scoreFormat = scoreFormat.rawValue
            )
        )
    }

    fun toSettings() {
        navigator.navigate(Routes.Settings)
    }

    fun toListStyleSettings() {
        navigator.navigate(Routes.ListStyleSettings)
    }

    fun toCustomLists() {
        navigator.navigate(Routes.CustomLists)
    }

    fun toTranslations() {
        navigator.navigate(Routes.Translations)
    }

    fun toReorderFavorites(
        userId: Int?,
        type: FavoritesType
    ) {
        navigator.navigate(
            Routes.ReorderFavorites(
                userId = userId,
                type = type
            )
        )
    }

    companion object {
        @Composable
        fun rememberNavActionManager(
            navigator: INavigator = PreviewNavigator()
        ) = remember {
            NavActionManager(navigator)
        }
    }
}