package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.refetchPolicy
import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.UserApi
import com.axiel7.anihyou.data.model.asDataResult
import com.axiel7.anihyou.data.model.asPagedResult
import com.axiel7.anihyou.data.model.stats.overview.toOverviewStats
import com.axiel7.anihyou.type.ActivitySort
import com.axiel7.anihyou.type.MediaListOptionsInput
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.type.UserStaffNameLanguage
import com.axiel7.anihyou.type.UserStatisticsSort
import com.axiel7.anihyou.type.UserTitleLanguage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: UserApi,
    private val defaultPreferencesRepository: DefaultPreferencesRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUnreadNotificationCount() = defaultPreferencesRepository.accessToken
        .filterNotNull()
        .flatMapLatest {
            api.unreadNotificationCountQuery()
                .fetchPolicy(FetchPolicy.NetworkOnly)
                .refetchPolicy(FetchPolicy.NetworkFirst)
                .watch()
                .map {
                    it.data?.Viewer?.unreadNotificationCount
                }
        }

    fun getViewerSettings() = api
        .viewerSettingsQuery()
        .watch()
        .asDataResult {
            it.Viewer?.userSettings
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
            it.UpdateUser?.userSettings
        }

    fun getMyUserInfo() = api
        .viewerUserInfoQuery()
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
        page: Int,
        perPage: Int = 25,
    ) = api
        .userActivityQuery(userId, sort, page, perPage)
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.activities?.filterNotNull().orEmpty()
        }

    fun getOverviewStats(
        userId: Int,
        mediaType: MediaType,
    ) = when (mediaType) {
        MediaType.ANIME -> api
            .userStatsAnimeOverviewQuery(userId)
            .watch()
            .asDataResult {
                it.User?.statistics?.anime?.toOverviewStats(
                    scoreFormat = it.User.mediaListOptions?.scoreFormat ?: ScoreFormat.UNKNOWN__
                )
            }

        MediaType.MANGA -> api
            .userStatsMangaOverviewQuery(userId)
            .watch()
            .asDataResult {
                it.User?.statistics?.manga?.toOverviewStats(
                    scoreFormat = it.User.mediaListOptions?.scoreFormat ?: ScoreFormat.UNKNOWN__
                )
            }

        else -> emptyFlow()
    }

    fun getGenresStats(
        userId: Int,
        mediaType: MediaType,
        sort: UserStatisticsSort,
    ) = when (mediaType) {
        MediaType.ANIME -> api
            .userStatsAnimeGenresQuery(userId, listOf(sort))
            .watch()
            .asDataResult { data ->
                data.User?.statistics?.anime?.genres?.filterNotNull()?.map { it.genreStat }
            }

        MediaType.MANGA -> api
            .userStatsMangaGenresQuery(userId, listOf(sort))
            .watch()
            .asDataResult { data ->
                data.User?.statistics?.manga?.genres?.filterNotNull()?.map { it.genreStat }
            }

        else -> emptyFlow()
    }

    fun getTagsStats(
        userId: Int,
        mediaType: MediaType,
        sort: UserStatisticsSort,
    ) = when (mediaType) {
        MediaType.ANIME -> api
            .userStatsAnimeTagsQuery(userId, listOf(sort))
            .watch()
            .asDataResult { data ->
                data.User?.statistics?.anime?.tags?.filterNotNull()?.map { it.tagStat }
            }

        MediaType.MANGA -> api
            .userStatsMangaTagsQuery(userId, listOf(sort))
            .watch()
            .asDataResult { data ->
                data.User?.statistics?.manga?.tags?.filterNotNull()?.map { it.tagStat }
            }

        else -> emptyFlow()
    }

    fun getStaffStats(
        userId: Int,
        mediaType: MediaType,
        sort: UserStatisticsSort,
    ) = when (mediaType) {
        MediaType.ANIME -> api
            .userStatsAnimeStaffQuery(userId, listOf(sort))
            .watch()
            .asDataResult { data ->
                data.User?.statistics?.anime?.staff?.filterNotNull()?.map { it.staffStat }
            }

        MediaType.MANGA -> api
            .userStatsMangaStaffQuery(userId, listOf(sort))
            .watch()
            .asDataResult { data ->
                data.User?.statistics?.manga?.staff?.filterNotNull()?.map { it.staffStat }
            }

        else -> emptyFlow()
    }

    fun getVoiceActorsStats(
        userId: Int,
        sort: UserStatisticsSort
    ) = api
        .userStatsVoiceActorsQuery(userId, listOf(sort))
        .watch()
        .asDataResult { data ->
            data.User?.statistics?.anime?.voiceActors?.filterNotNull()?.map { it.voiceActorStat }
        }

    fun getStudiosStats(
        userId: Int,
        sort: UserStatisticsSort
    ) = api
        .userStatsStudiosQuery(userId, listOf(sort))
        .watch()
        .asDataResult { data ->
            data.User?.statistics?.anime?.studios?.filterNotNull()?.map { it.studioStat }
        }

    fun getFollowers(
        userId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .followersQuery(userId, page, perPage)
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.followers?.filterNotNull().orEmpty()
        }

    fun getFollowing(
        userId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .followingsQuery(userId, page, perPage)
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.following?.filterNotNull().orEmpty()
        }
}