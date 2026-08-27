package com.axiel7.anihyou.core.domain.repository

import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.model.media.AnimeThemes
import com.axiel7.anihyou.core.model.media.AnimeThemes.Companion.toBo
import com.axiel7.anihyou.core.model.media.ChartType
import com.axiel7.anihyou.core.model.media.MediaCharactersAndStaff
import com.axiel7.anihyou.core.model.media.MediaRelationsAndRecommendations
import com.axiel7.anihyou.core.model.media.isActive
import com.axiel7.anihyou.core.network.MediaDetailsQuery
import com.axiel7.anihyou.core.network.api.MalApi
import com.axiel7.anihyou.core.network.api.MediaApi
import com.axiel7.anihyou.core.network.api.model.CountryOfOriginDto
import com.axiel7.anihyou.core.network.fragment.ExploreMedia
import com.axiel7.anihyou.core.network.type.AiringSort
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.RecommendationRating
import com.axiel7.anihyou.core.network.type.RecommendationSort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepository (
    private val api: MediaApi,
    private val malApi: MalApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    fun getAiringAnimesPage(
        airingAtGreater: Long? = null,
        airingAtLesser: Long? = null,
        sort: List<AiringSort> = listOf(AiringSort.TIME),
        onMyList: Boolean? = null,
        isAdult: Boolean = false,
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
            val list = data.Page?.airingSchedules?.mapNotNull { it?.media?.exploreMedia }.orEmpty()
            fun ExploreMedia.adultFilter() =
                if (!isAdult) basicMediaDetails.isAdult == false else true
            when (onMyList) {
                true -> list.filter { it.mediaListEntry != null && it.adultFilter() }
                false -> list.filter { it.mediaListEntry == null && it.adultFilter() }
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
            data.Page?.media?.mapNotNull { it?.exploreMedia }
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
        isAdult: Boolean? = null,
        page: Int,
        perPage: Int = 25,
    ) = api
        .seasonalAnimeQuery(animeSeason.toDto(), sort, isAdult, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            data.Page?.media?.mapNotNull { it?.exploreMedia }.orEmpty()
        }

    fun getMediaSortedPage(
        mediaType: MediaType,
        sort: List<MediaSort>,
        country: CountryOfOriginDto? = null,
        isAdult: Boolean? = null,
        page: Int,
        perPage: Int = 25,
    ) = api
        .mediaSortedQuery(mediaType, sort, country, isAdult, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            data.Page?.media?.mapNotNull { it?.exploreMedia }.orEmpty()
        }

    fun getMediaChartPage(
        type: ChartType,
        isAdult: Boolean? = null,
        page: Int,
        perPage: Int = 25,
    ) = api
        .mediaChartQuery(
            type = type.mediaType,
            sort = listOf(type.mediaSort),
            status = type.mediaStatus,
            format = type.mediaFormat,
            country = type.country,
            isAdult = isAdult,
            page = page,
            perPage = perPage
        )
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.media?.filterNotNull().orEmpty()
        }

    fun getMediaDetails(mediaId: Int) = api
        .mediaDetailsQuery(mediaId)
        .toFlow()
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

    fun getMediaCharactersPage(
        mediaId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .mediaCharactersQuery(mediaId, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Media?.characters?.pageInfo?.commonPage }) { data ->
            data.Media?.characters?.edges?.mapNotNull { it?.mediaCharacter }.orEmpty()
        }

    fun getBasicMediaDetails(mediaId: Int) = api
        .basicMediaDetails(mediaId)
        .toFlow()
        .asDataResult { it.Media?.mediaListEntry?.commonMediaListEntry }

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

    fun saveRecommendation(
        mediaId: Int,
        mediaRecommendationId: Int,
        rating: RecommendationRating?
    ) = api.saveRecommendationMutation(
        mediaId = mediaId,
        mediaRecommendationId = mediaRecommendationId,
        rating = rating
    ).toFlow().asDataResult()


    fun mediaRecommendations(
        onList: Boolean?,
        sort: List<RecommendationSort>?,
        page: Int,
        perPage: Int,
        fetchFromNetwork: Boolean = false,
    ) = api
        .mediaRecommendationsQuery(
            onList = onList,
            sort = sort,
            page = page,
            perPage = perPage,
            fetchFromNetwork = fetchFromNetwork
        )
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.recommendations?.filterNotNull().orEmpty()
        }

    // MyAnimeList endpoints

    suspend fun getAnimeThemes(idMal: Int) = withContext(Dispatchers.IO) {
        malApi.getAnimeThemes(idMal)?.let { result ->
            AnimeThemes(
                openingThemes = result.openingThemes?.map { it.toBo() },
                endingThemes = result.endingThemes?.map { it.toBo() },
            )
        }
    }
}