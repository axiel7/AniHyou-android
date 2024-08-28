package com.axiel7.anihyou.data.repository

import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.apollographql.apollo.cache.normalized.watch
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.data.api.MalApi
import com.axiel7.anihyou.data.api.MediaApi
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.data.model.media.MediaCharactersAndStaff
import com.axiel7.anihyou.data.model.media.MediaRelationsAndRecommendations
import com.axiel7.anihyou.data.model.media.isActive
import com.axiel7.anihyou.type.AiringSort
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val api: MediaApi,
    private val malApi: MalApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    fun getAiringAnimesPage(
        airingAtGreater: Long? = null,
        airingAtLesser: Long? = null,
        sort: List<AiringSort> = listOf(AiringSort.TIME),
        onMyList: Boolean? = null,
        displayAdult: Boolean = false,
        page: Int,
        perPage: Int = 25,
    ) = api
        .airingAnimesQuery(
            airingAtGreater = airingAtGreater,
            airingAtLesser = airingAtLesser,
            sort = sort,
            page = page,
            perPage = perPage,
        )
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            val list = data.Page?.airingSchedules?.filterNotNull().orEmpty()
            fun AiringAnimesQuery.AiringSchedule.adultFilter() =
                if (!displayAdult) media?.isAdult == false else true
            when (onMyList) {
                true -> list.filter { it.media?.mediaListEntry != null && it.adultFilter() }
                false -> list.filter { it.media?.mediaListEntry == null && it.adultFilter() }
                null -> list.filter { it.adultFilter() }
            }
        }

    fun getAiringAnimeOnMyListPage(
        page: Int,
        perPage: Int = 25,
    ) = api
        .airingOnMyListQuery(page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            data.Page?.media?.filterNotNull()
                ?.filter {
                    it.nextAiringEpisode != null
                            && it.mediaListEntry?.basicMediaListEntry?.status?.isActive() == true
                }
                ?.sortedBy { it.nextAiringEpisode?.timeUntilAiring }
                .orEmpty()
        }

    fun getSeasonalAnimePage(
        animeSeason: AnimeSeason,
        sort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC),
        page: Int,
        perPage: Int = 25,
    ) = api
        .seasonalAnimeQuery(animeSeason, sort, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.media?.filterNotNull().orEmpty()
        }

    fun getMediaSortedPage(
        mediaType: MediaType,
        sort: List<MediaSort>,
        page: Int,
        perPage: Int = 25,
    ) = api
        .mediaSortedQuery(mediaType, sort, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.media?.filterNotNull().orEmpty()
        }

    fun getMediaChartPage(
        type: ChartType,
        page: Int,
        perPage: Int = 25,
    ) = api
        .mediaChartQuery(
            type = type.mediaType,
            sort = listOf(type.mediaSort),
            status = type.mediaStatus,
            format = type.mediaFormat,
            page = page,
            perPage = perPage
        )
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.media?.filterNotNull().orEmpty()
        }

    fun getMediaDetails(mediaId: Int) = api
        .mediaDetailsQuery(mediaId)
        .watch()
        .asDataResult { it.Media }

    suspend fun updateMediaDetailsCache(media: MediaDetailsQuery.Media) {
        api.updateMediaDetailsCache(
            data = MediaDetailsQuery.Data(media)
        )
    }

    fun getMediaCharactersAndStaff(mediaId: Int) = api
        .mediaCharactersAndStaffQuery(mediaId)
        .toFlow()
        .asDataResult {
            MediaCharactersAndStaff(
                characters = it.Media?.characters?.edges?.filterNotNull().orEmpty(),
                staff = it.Media?.staff?.edges?.filterNotNull().orEmpty()
            )
        }

    fun getMediaRelationsAndRecommendations(mediaId: Int) = api
        .mediaRelationsAndRecommendationsQuery(mediaId)
        .toFlow()
        .asDataResult {
            MediaRelationsAndRecommendations(
                relations = it.Media?.relations?.edges?.filterNotNull().orEmpty(),
                recommendations = it.Media?.recommendations?.nodes?.filterNotNull().orEmpty()
            )
        }

    fun getMediaStats(mediaId: Int) = api
        .mediaStatsQuery(mediaId)
        .toFlow()
        .asDataResult { it.Media }

    fun getMediaFollowing(
        mediaId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .mediaFollowingQuery(mediaId, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.mediaList?.filterNotNull().orEmpty()
        }

    fun getMediaReviewsPage(
        mediaId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .mediaReviewsQuery(mediaId, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Media?.reviews?.pageInfo?.commonPage }) {
            it.Media?.reviews?.nodes?.filterNotNull().orEmpty()
        }

    fun getMediaThreadsPage(
        mediaId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .mediaThreadsQuery(mediaId, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.threads?.filterNotNull().orEmpty()
        }

    fun getMediaActivityPage(
        mediaId: Int,
        userId: Int? = null,
        page: Int,
        perPage: Int = 25,
    ) = api
        .mediaActivityQuery(mediaId, userId, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            data.Page?.activities?.mapNotNull { it?.listActivityFragment }.orEmpty()
        }

    // widget

    suspend fun getAiringWidgetData(
        page: Int,
        perPage: Int = 25,
    ) = api
        .airingWidgetQuery(page, perPage)
        .fetchPolicy(FetchPolicy.NetworkFirst)
        .execute()
        .asDataResult { data ->
            data.Page?.media?.filterNotNull()
                ?.filter {
                        it.nextAiringEpisode != null
                                && it.mediaListEntry?.status?.isActive() == true
                    }
                ?.sortedBy { it.nextAiringEpisode?.timeUntilAiring }
                .orEmpty()
        }

    // MyAnimeList endpoints

    suspend fun getAnimeThemes(idMal: Int) = withContext(Dispatchers.IO) {
        malApi.getAnimeThemes(idMal)
    }
}