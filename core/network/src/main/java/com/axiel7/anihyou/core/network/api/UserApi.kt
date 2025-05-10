package com.axiel7.anihyou.core.network.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.axiel7.anihyou.core.network.FollowersQuery
import com.axiel7.anihyou.core.network.FollowingsQuery
import com.axiel7.anihyou.core.network.SearchUserQuery
import com.axiel7.anihyou.core.network.ToggleFollowMutation
import com.axiel7.anihyou.core.network.UnreadNotificationCountQuery
import com.axiel7.anihyou.core.network.UpdateUserMutation
import com.axiel7.anihyou.core.network.UserActivityQuery
import com.axiel7.anihyou.core.network.UserBasicInfoQuery
import com.axiel7.anihyou.core.network.UserStatsAnimeGenresQuery
import com.axiel7.anihyou.core.network.UserStatsAnimeOverviewQuery
import com.axiel7.anihyou.core.network.UserStatsAnimeStaffQuery
import com.axiel7.anihyou.core.network.UserStatsAnimeTagsQuery
import com.axiel7.anihyou.core.network.UserStatsMangaGenresQuery
import com.axiel7.anihyou.core.network.UserStatsMangaOverviewQuery
import com.axiel7.anihyou.core.network.UserStatsMangaStaffQuery
import com.axiel7.anihyou.core.network.UserStatsMangaTagsQuery
import com.axiel7.anihyou.core.network.UserStatsStudiosQuery
import com.axiel7.anihyou.core.network.UserStatsVoiceActorsQuery
import com.axiel7.anihyou.core.network.ViewerSettingsQuery
import com.axiel7.anihyou.core.network.ViewerUserInfoQuery
import com.axiel7.anihyou.core.network.type.ActivitySort
import com.axiel7.anihyou.core.network.type.MediaListOptionsInput
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.network.type.UserStaffNameLanguage
import com.axiel7.anihyou.core.network.type.UserStatisticsSort
import com.axiel7.anihyou.core.network.type.UserTitleLanguage

class UserApi (
    private val client: ApolloClient
) {
    fun searchUserQuery(
        query: String,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            SearchUserQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                search = Optional.present(query)
            )
        )

    fun unreadNotificationCountQuery() = client.query(UnreadNotificationCountQuery())

    fun viewerSettingsQuery() = client.query(ViewerSettingsQuery())

    fun updateUserMutation(
        displayAdultContent: Boolean?,
        titleLanguage: UserTitleLanguage?,
        staffNameLanguage: UserStaffNameLanguage?,
        scoreFormat: ScoreFormat?,
        airingNotifications: Boolean?,
        animeListOptions: MediaListOptionsInput?,
        mangaListOptions: MediaListOptionsInput?,
    ) = client
        .mutation(
            UpdateUserMutation(
                displayAdultContent = Optional.presentIfNotNull(displayAdultContent),
                titleLanguage = Optional.presentIfNotNull(titleLanguage),
                staffNameLanguage = Optional.presentIfNotNull(staffNameLanguage),
                scoreFormat = Optional.presentIfNotNull(scoreFormat),
                airingNotifications = Optional.presentIfNotNull(airingNotifications),
                animeListOptions = Optional.presentIfNotNull(animeListOptions),
                mangaListOptions = Optional.presentIfNotNull(mangaListOptions),
            )
        )

    fun viewerUserInfoQuery() = client.query(ViewerUserInfoQuery())

    fun userBasicInfoQuery(
        userId: Int?,
        username: String?,
    ) = client
        .query(
            UserBasicInfoQuery(
                userId = Optional.presentIfNotNull(userId),
                name = Optional.presentIfNotNull(username)
            )
        )

    fun toggleFollowMutation(userId: Int) = client
        .mutation(
            ToggleFollowMutation(
                userId = Optional.present(userId)
            )
        )

    fun userActivityQuery(
        userId: Int,
        sort: List<ActivitySort>,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            UserActivityQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                userId = Optional.present(userId),
                sort = Optional.present(sort)
            )
        )

    fun userStatsAnimeOverviewQuery(userId: Int) = client
        .query(
            UserStatsAnimeOverviewQuery(
                userId = Optional.present(userId)
            )
        )

    fun userStatsMangaOverviewQuery(userId: Int) = client
        .query(
            UserStatsMangaOverviewQuery(
                userId = Optional.present(userId)
            )
        )

    fun userStatsAnimeGenresQuery(
        userId: Int,
        sort: List<UserStatisticsSort>?
    ) = client
        .query(
            UserStatsAnimeGenresQuery(
                userId = Optional.present(userId),
                sort = Optional.presentIfNotNull(sort),
            )
        )

    fun userStatsMangaGenresQuery(
        userId: Int,
        sort: List<UserStatisticsSort>?
    ) = client
        .query(
            UserStatsMangaGenresQuery(
                userId = Optional.present(userId),
                sort = Optional.presentIfNotNull(sort),
            )
        )

    fun userStatsAnimeTagsQuery(
        userId: Int,
        sort: List<UserStatisticsSort>?
    ) = client
        .query(
            UserStatsAnimeTagsQuery(
                userId = Optional.present(userId),
                sort = Optional.presentIfNotNull(sort),
            )
        )

    fun userStatsMangaTagsQuery(
        userId: Int,
        sort: List<UserStatisticsSort>?
    ) = client
        .query(
            UserStatsMangaTagsQuery(
                userId = Optional.present(userId),
                sort = Optional.presentIfNotNull(sort),
            )
        )

    fun userStatsAnimeStaffQuery(
        userId: Int,
        sort: List<UserStatisticsSort>?
    ) = client
        .query(
            UserStatsAnimeStaffQuery(
                userId = Optional.present(userId),
                sort = Optional.presentIfNotNull(sort),
            )
        )

    fun userStatsMangaStaffQuery(
        userId: Int,
        sort: List<UserStatisticsSort>?
    ) = client
        .query(
            UserStatsMangaStaffQuery(
                userId = Optional.present(userId),
                sort = Optional.presentIfNotNull(sort),
            )
        )

    fun userStatsVoiceActorsQuery(
        userId: Int,
        sort: List<UserStatisticsSort>?
    ) = client
        .query(
            UserStatsVoiceActorsQuery(
                userId = Optional.present(userId),
                sort = Optional.presentIfNotNull(sort),
            )
        )

    fun userStatsStudiosQuery(
        userId: Int,
        sort: List<UserStatisticsSort>?
    ) = client
        .query(
            UserStatsStudiosQuery(
                userId = Optional.present(userId),
                sort = Optional.presentIfNotNull(sort),
            )
        )

    fun followersQuery(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = client
        .query(
            FollowersQuery(
                userId = userId,
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun followingsQuery(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = client
        .query(
            FollowingsQuery(
                userId = userId,
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )
}