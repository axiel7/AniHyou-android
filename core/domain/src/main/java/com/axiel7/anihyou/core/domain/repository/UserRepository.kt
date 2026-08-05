package com.axiel7.anihyou.core.domain.repository

import com.apollographql.apollo.api.Optional
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.apollographql.cache.normalized.refetchPolicy
import com.axiel7.anihyou.core.model.stats.overview.toOverviewStats
import com.axiel7.anihyou.core.network.api.UserApi
import com.axiel7.anihyou.core.network.type.ActivitySort
import com.axiel7.anihyou.core.network.type.MediaListOptionsInput
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.network.type.UserStaffNameLanguage
import com.axiel7.anihyou.core.network.type.UserStatisticsSort
import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val api: UserApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    fun getUnreadNotificationCount() = api
        .unreadNotificationCountQuery()
        .fetchPolicy(FetchPolicy.NetworkOnly)
        .refetchPolicy(FetchPolicy.NetworkFirst)
        .toFlow()
        .map {
            it.data?.Viewer?.unreadNotificationCount
        }

    fun getViewerSettings() = api
        .viewerSettingsQuery()
        .toFlow()
        .asDataResult {
            it.Viewer?.userSettings
        }

    suspend fun updateUser(
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
        .execute()
        .asDataResult {
            it.UpdateUser?.userSettings
        }

    suspend fun updateCustomLists(
        animeList: List<String>? = null,
        mangaList: List<String>? = null,
    ) = updateUser(
        animeListOptions = animeList?.let {
            MediaListOptionsInput(
                customLists = Optional.present(animeList)
            )
        },
        mangaListOptions = mangaList?.let {
            MediaListOptionsInput(
                customLists = Optional.present(mangaList)
            )
        }
    )

    fun getMyUserInfo(fetchFromNetwork: Boolean = false) = api
        .viewerUserInfoQuery()
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asDataResult {
            it.Viewer?.userInfo
        }

    fun getUserInfo(
        userId: Int? = null,
        username: String? = null,
        fetchFromNetwork: Boolean = false
    ) = api
        .userBasicInfoQuery(userId, username)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asDataResult {
            it.User?.userInfo
        }

    suspend fun toggleFollow(userId: Int) = api
        .toggleFollowMutation(userId)
        .execute()
        .asDataResult {
            it.ToggleFollow
        }

    fun getUserActivity(
        userId: Int,
        sort: List<ActivitySort> = listOf(ActivitySort.ID_DESC),
        fetchFromNetwork: Boolean,
        page: Int,
        perPage: Int = 25,
    ) = api
        .userActivityQuery(userId, sort, page, perPage)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.activities?.filterNotNull().orEmpty()
        }

    fun getOverviewStats(
        userId: Int,
        mediaType: MediaType,
        fetchFromNetwork: Boolean,
    ) = when (mediaType) {
        MediaType.ANIME -> api
            .userStatsAnimeOverviewQuery(userId)
            .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
            .toFlow()
            .asDataResult {
                it.User?.statistics?.anime?.toOverviewStats(
                    scoreFormat = it.User?.mediaListOptions?.scoreFormat ?: ScoreFormat.UNKNOWN__
                )
            }

        MediaType.MANGA -> api
            .userStatsMangaOverviewQuery(userId)
            .toFlow()
            .asDataResult {
                it.User?.statistics?.manga?.toOverviewStats(
                    scoreFormat = it.User?.mediaListOptions?.scoreFormat ?: ScoreFormat.UNKNOWN__
                )
            }

        else -> emptyFlow()
    }

    fun getGenresStats(
        userId: Int,
        mediaType: MediaType,
        sort: UserStatisticsSort,
        fetchFromNetwork: Boolean,
    ) = when (mediaType) {
        MediaType.ANIME -> api
            .userStatsAnimeGenresQuery(userId, listOf(sort))
            .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
            .toFlow()
            .asDataResult { data ->
                data.User?.statistics?.anime?.genres?.filterNotNull()?.map { it.genreStat }
            }

        MediaType.MANGA -> api
            .userStatsMangaGenresQuery(userId, listOf(sort))
            .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
            .toFlow()
            .asDataResult { data ->
                data.User?.statistics?.manga?.genres?.filterNotNull()?.map { it.genreStat }
            }

        else -> emptyFlow()
    }

    fun getTagsStats(
        userId: Int,
        mediaType: MediaType,
        sort: UserStatisticsSort,
        fetchFromNetwork: Boolean,
    ) = when (mediaType) {
        MediaType.ANIME -> api
            .userStatsAnimeTagsQuery(userId, listOf(sort))
            .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
            .toFlow()
            .asDataResult { data ->
                data.User?.statistics?.anime?.tags?.filterNotNull()?.map { it.tagStat }
            }

        MediaType.MANGA -> api
            .userStatsMangaTagsQuery(userId, listOf(sort))
            .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
            .toFlow()
            .asDataResult { data ->
                data.User?.statistics?.manga?.tags?.filterNotNull()?.map { it.tagStat }
            }

        else -> emptyFlow()
    }

    fun getStaffStats(
        userId: Int,
        mediaType: MediaType,
        sort: UserStatisticsSort,
        fetchFromNetwork: Boolean,
    ) = when (mediaType) {
        MediaType.ANIME -> api
            .userStatsAnimeStaffQuery(userId, listOf(sort))
            .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
            .toFlow()
            .asDataResult { data ->
                data.User?.statistics?.anime?.staff?.filterNotNull()?.map { it.staffStat }
            }

        MediaType.MANGA -> api
            .userStatsMangaStaffQuery(userId, listOf(sort))
            .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
            .toFlow()
            .asDataResult { data ->
                data.User?.statistics?.manga?.staff?.filterNotNull()?.map { it.staffStat }
            }

        else -> emptyFlow()
    }

    fun getVoiceActorsStats(
        userId: Int,
        sort: UserStatisticsSort,
        fetchFromNetwork: Boolean,
    ) = api
        .userStatsVoiceActorsQuery(userId, listOf(sort))
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asDataResult { data ->
            data.User?.statistics?.anime?.voiceActors?.filterNotNull()?.map { it.voiceActorStat }
        }

    fun getStudiosStats(
        userId: Int,
        sort: UserStatisticsSort,
        fetchFromNetwork: Boolean,
    ) = api
        .userStatsStudiosQuery(userId, listOf(sort))
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asDataResult { data ->
            data.User?.statistics?.anime?.studios?.filterNotNull()?.map { it.studioStat }
        }

    fun getFollowers(
        userId: Int,
        page: Int,
        perPage: Int = 25,
        fetchFromNetwork: Boolean,
    ) = api
        .followersQuery(userId, page, perPage)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.followers?.filterNotNull().orEmpty()
        }

    fun getFollowing(
        userId: Int,
        page: Int,
        perPage: Int = 25,
        fetchFromNetwork: Boolean,
    ) = api
        .followingsQuery(userId, page, perPage)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.following?.filterNotNull().orEmpty()
        }
}