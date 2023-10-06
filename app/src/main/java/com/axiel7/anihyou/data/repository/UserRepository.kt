package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.UserApi
import com.axiel7.anihyou.type.ActivitySort
import com.axiel7.anihyou.type.MediaListOptionsInput
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.type.UserStaffNameLanguage
import com.axiel7.anihyou.type.UserTitleLanguage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val api: UserApi,
    private val defaultPreferencesRepository: DefaultPreferencesRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUnreadNotificationCount() = defaultPreferencesRepository.accessToken
        .filterNotNull()
        .flatMapLatest {
            api.unreadNotificationCountQuery()
                .watch()
                .map {
                    it.data?.Viewer?.unreadNotificationCount ?: 0
                }
        }

    fun getUserOptions() = api
        .userOptionsQuery()
        .watch()
        .asDataResult {
            it.Viewer?.userOptionsFragment
        }

    fun updateUser(
        displayAdultContent: Boolean? = null,
        titleLanguage: UserTitleLanguage? = null,
        staffNameLanguage: UserStaffNameLanguage? = null,
        scoreFormat: ScoreFormat? = null,
        airingNotifications: Boolean? = null,
        animeListOptions: MediaListOptionsInput? = null,
        mangaListOptions: MediaListOptionsInput? = null,
    ) = api
        .updateUserMutation(
            displayAdultContent,
            titleLanguage,
            staffNameLanguage,
            scoreFormat,
            airingNotifications,
            animeListOptions,
            mangaListOptions
        )
        .toFlow()
        .asDataResult {
            it.UpdateUser?.userOptionsFragment
        }

    fun getMyUserInfo() = api
        .viewerQuery()
        .watch()
        .asDataResult {
            it.Viewer?.userInfo
        }

    fun getUserInfo(
        userId: Int? = null,
        username: String? = null,
    ) = api
        .userBasicInfoQuery(userId, username)
        .watch()
        .asDataResult {
            it.User?.userInfo
        }

    fun toggleFollow(userId: Int) = api
        .toggleFollowMutation(userId)
        .toFlow()
        .asDataResult {
            it.ToggleFollow
        }

    fun getUserActivity(
        userId: Int,
        sort: List<ActivitySort> = listOf(ActivitySort.ID_DESC),
        page: Int = 1,
        perPage: Int = 25,
    ) = api
        .userActivityQuery(userId, sort, page, perPage)
        .watch()
        .asDataResult {
            PageResult(
                list = it.Page?.activities?.filterNotNull().orEmpty(),
                nextPage = if (it.Page?.pageInfo?.hasNextPage == true)
                    it.Page.pageInfo.currentPage?.plus(1)
                else null
            )
        }

    fun getOverviewAnimeStats(userId: Int) = api
        .userStatsAnimeOverviewQuery(userId)
        .watch()
        .asDataResult {
            it.User?.statistics?.anime
        }

    fun getOverviewMangaStats(userId: Int) = api
        .userStatsMangaOverviewQuery(userId)
        .watch()
        .asDataResult {
            it.User?.statistics?.manga
        }

    fun getFollowers(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = api
        .followersQuery(userId, page, perPage)
        .watch()
        .asDataResult {
            PageResult(
                list = it.Page?.followers?.filterNotNull().orEmpty(),
                nextPage = if (it.Page?.pageInfo?.hasNextPage == true)
                    it.Page.pageInfo.currentPage?.plus(1)
                else null
            )
        }

    fun getFollowing(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = api
        .followingsQuery(userId, page, perPage)
        .watch()
        .asDataResult {
            PageResult(
                list = it.Page?.following?.filterNotNull().orEmpty(),
                nextPage = if (it.Page?.pageInfo?.hasNextPage == true)
                    it.Page.pageInfo.currentPage?.plus(1)
                else null
            )
        }
}