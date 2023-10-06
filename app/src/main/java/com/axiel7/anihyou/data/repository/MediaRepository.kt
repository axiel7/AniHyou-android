package com.axiel7.anihyou.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.UserCurrentAnimeListQuery
import com.axiel7.anihyou.data.api.MediaApi
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.data.model.media.MediaCharactersAndStaff
import com.axiel7.anihyou.data.model.media.MediaRelationsAndRecommendations
import com.axiel7.anihyou.data.paging.AiringAnimePagingSourceFactory
import com.axiel7.anihyou.data.paging.AnimeSeasonalPagingSourceFactory
import com.axiel7.anihyou.data.paging.MediaChartPagingSourceFactory
import com.axiel7.anihyou.type.AiringSort
import com.axiel7.anihyou.type.MediaStatus
import javax.inject.Inject

class MediaRepository @Inject constructor(
    private val api: MediaApi,
    private val airingAnimePagingSourceFactory: AiringAnimePagingSourceFactory,
    private val mediaChartPagingSourceFactory: MediaChartPagingSourceFactory,
    private val animeSeasonalPagingSourceFactory: AnimeSeasonalPagingSourceFactory,
) {

    fun getAiringAnimesPage(
        airingAtGreater: Long,
        airingAtLesser: Long,
        sort: List<AiringSort> = listOf(AiringSort.TIME)
    ) = Pager(
        config = PagingConfig(pageSize = 25),
    ) {
        airingAnimePagingSourceFactory.create(airingAtGreater, airingAtLesser, sort)
    }.flow

    fun getMediaChartPage(
        type: ChartType
    ) = Pager(
        config = PagingConfig(pageSize = 25)
    ) {
        mediaChartPagingSourceFactory.create(type)
    }.flow

    fun getSeasonalAnimePage(
        animeSeason: AnimeSeason
    ) = Pager(
        config = PagingConfig(pageSize = 25),
    ) {
        animeSeasonalPagingSourceFactory.create(animeSeason)
    }.flow

    fun getMediaDetails(mediaId: Int) = api
        .mediaDetailsQuery(mediaId)
        .watch()
        .asDataResult { it.Media }

    fun getMediaCharactersAndStaff(mediaId: Int) = api
        .mediaCharactersAndStaffQuery(mediaId)
        .watch()
        .asDataResult {
            MediaCharactersAndStaff(
                characters = it.Media?.characters?.edges?.filterNotNull().orEmpty(),
                staff = it.Media?.staff?.edges?.filterNotNull().orEmpty()
            )
        }

    fun getMediaRelationsAndRecommendations(mediaId: Int) = api
        .mediaRelationsAndRecommendationsQuery(mediaId)
        .watch()
        .asDataResult {
            MediaRelationsAndRecommendations(
                relations = it.Media?.relations?.edges?.filterNotNull().orEmpty(),
                recommendations = it.Media?.recommendations?.nodes?.filterNotNull().orEmpty()
            )
        }

    fun getMediaStats(mediaId: Int) = api
        .mediaStatsQuery(mediaId)
        .watch()
        .asDataResult { it.Media }


    // widget

    suspend fun getUserCurrentAnimeList(userId: Int): List<UserCurrentAnimeListQuery.MediaList>? {
        val response = api.userCurrentAnimeListQuery(userId).execute()
        return if (response.hasErrors()) null
        else {
            response.data?.Page?.mediaList?.filterNotNull()?.let { mediaList ->
                return mediaList
                    .filter { it.media?.status == MediaStatus.RELEASING }
                    .sortedBy { it.media?.nextAiringEpisode?.timeUntilAiring }
            }
            return null
        }
    }
}