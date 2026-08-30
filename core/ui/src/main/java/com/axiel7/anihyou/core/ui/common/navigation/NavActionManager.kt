package com.axiel7.anihyou.core.ui.common.navigation

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.model.CurrentListType
import com.axiel7.anihyou.core.model.FavoritesType
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.model.media.ChartType
import com.axiel7.anihyou.core.model.thread.ChildComment
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat

@Immutable
class NavActionManager(
    private val navigator: INavigator,
) {
    fun navigate(route: Route) {
        navigator.navigate(route)
    }

    fun goBack() {
        navigator.goBack()
    }

    fun toMediaDetails(id: Int) {
        navigator.navigate(Route.MediaDetails(id))
    }

    fun toMediaActivity(mediaId: Int) {
        navigator.navigate(Route.MediaActivity(mediaId))
    }

    fun toMediaCharacters(mediaId: Int) {
        navigator.navigate(Route.MediaCharacters(mediaId))
    }

    fun toCharacterDetails(id: Int) {
        navigator.navigate(Route.CharacterDetails(id))
    }

    fun toStaffDetails(id: Int) {
        navigator.navigate(Route.StaffDetails(id))
    }

    fun toStudioDetails(id: Int) {
        navigator.navigate(Route.StudioDetails(id))
    }

    fun toUserDetails(id: Int) {
        navigator.navigate(Route.UserDetails(id = id, userName = null))
    }

    fun toUserDetails(userId: Int?, username: String?) {
        navigator.navigate(Route.UserDetails(userId, username))
    }

    fun toActivityDetails(id: Int) {
        navigator.navigate(Route.ActivityDetails(id))
    }

    fun toThreadDetails(id: Int) {
        navigator.navigate(Route.ThreadDetails(id))
    }

    fun toThreadCommentDetails(childComment: ChildComment) {
        navigator.navigate(Route.ThreadCommentDetails(childComment))
    }

    fun toReviewDetails(id: Int) {
        navigator.navigate(Route.ReviewDetails(id))
    }

    fun toFullscreenImage(url: String) {
        navigator.navigate(Route.FullScreenImage(url))
    }

    fun toSearch() {
        navigator.navigate(Route.Search(focus = true))
    }

    fun toSearchOnMyList(mediaType: MediaType) {
        navigator.navigate(
            Route.Search(mediaType = mediaType.rawValue, onList = true, focus = true)
        )
    }

    fun toGenreTag(
        mediaType: MediaType,
        genre: String?,
        tag: String?
    ) {
        navigator.navigate(
            Route.Search(mediaType = mediaType.rawValue, genre = genre, tag = tag)
        )
    }

    fun toAnimeSeason(season: AnimeSeason) {
        navigator.navigate(
            Route.SeasonAnime(season = season.season.rawValue, year = season.year)
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
        navigator.navigate(Route.Calendar)
    }

    fun toCurrentFullList(listType: CurrentListType) {
        navigator.navigate(Route.CurrentFullList(listType = listType))
    }

    fun toExplore(mediaType: MediaType, mediaSort: MediaSort) {
        navigator.navigate(
            Route.Search(mediaType = mediaType.rawValue, mediaSort = mediaSort.rawValue)
        )
    }

    fun toNotifications(unread: Int = 0) {
        navigator.navigate(Route.Notifications(unread))
    }

    fun toPublishNewActivity() {
        navigator.navigate(
            Route.PublishActivity(activityId = null, id = null, text = null)
        )
    }

    fun toPublishActivityReply(
        activityId: Int,
        replyId: Int?,
        text: String?
    ) {
        navigator.navigate(
            Route.PublishActivity(activityId = activityId, id = replyId, text = text)
        )
    }

    fun toPublishThreadComment(
        threadId: Int,
        commentId: Int?,
        text: String?
    ) {
        navigator.navigate(
            Route.PublishComment(threadId = threadId, id = commentId ?: 0, text = text)
        )
    }

    fun toPublishCommentReply(
        threadId: Int,
        parentCommentId: Int,
        commentId: Int?,
        text: String?
    ) {
        navigator.navigate(
            Route.PublishComment(
                threadId = threadId,
                parentCommentId = parentCommentId,
                id = commentId ?: 0,
                text = text
            )
        )
    }

    fun toMediaChart(type: ChartType) {
        navigator.navigate(Route.MediaChartList(type.name))
    }

    fun toUserMediaList(
        mediaType: MediaType,
        userId: Int,
        scoreFormat: ScoreFormat
    ) {
        navigator.navigate(
            Route.UserMediaList(
                mediaType = mediaType.rawValue,
                userId = userId,
                scoreFormat = scoreFormat.rawValue
            )
        )
    }

    fun toSettings() {
        navigator.navigate(Route.Settings)
    }

    fun toListStyleSettings() {
        navigator.navigate(Route.ListStyleSettings)
    }

    fun toCustomLists() {
        navigator.navigate(Route.CustomLists)
    }

    fun toTranslations() {
        navigator.navigate(Route.Translations)
    }

    fun toReorderFavorites(
        userId: Int,
        type: FavoritesType
    ) {
        navigator.navigate(
            Route.ReorderFavorites(
                userId = userId,
                type = type
            )
        )
    }

    fun toContributors() {
        navigator.navigate(Route.Contributors)
    }

    fun toPriorityColors() {
        navigator.navigate(Route.PriorityColors)
    }
}