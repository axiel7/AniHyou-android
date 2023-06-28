package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.AiringOnMyListQuery
import com.axiel7.anihyou.MediaCharactersAndStaffQuery
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.MediaRelationsAndRecommendationsQuery
import com.axiel7.anihyou.MediaReviewsQuery
import com.axiel7.anihyou.MediaSortedQuery
import com.axiel7.anihyou.MediaStatsQuery
import com.axiel7.anihyou.MediaThreadsQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.UserCurrentAnimeListQuery
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.MediaCharactersAndStaff
import com.axiel7.anihyou.data.model.media.MediaRelationsAndRecommendations
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import com.axiel7.anihyou.network.apolloClient
import com.axiel7.anihyou.type.AiringSort
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ThreadSort
import kotlinx.coroutines.flow.flow

object MediaRepository {

    fun getAiringAnimePage(
        airingAtGreater: Long? = null,
        airingAtLesser: Long? = null,
        sort: List<AiringSort> = listOf(AiringSort.TIME),
        page: Int = 1,
        perPage: Int = 15,
    ) = flow {
        emit(PagedResult.Loading)
        val response = AiringAnimesQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            sort = Optional.present(sort),
            airingAtGreater = Optional.presentIfNotNull(airingAtGreater?.toInt()),
            airingAtLesser = Optional.presentIfNotNull(airingAtLesser?.toInt()),
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val airingPage = response?.data?.Page
            if (airingPage != null) emit(PagedResult.Success(
                data = airingPage.airingSchedules?.filterNotNull().orEmpty(),
                nextPage = if (airingPage.pageInfo?.hasNextPage == true)
                    airingPage.pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Empty"))
        }
    }

    fun getAiringAnimeOnMyListPage(
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)
        val response = AiringOnMyListQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val airingAnime = response?.data?.Page?.media?.filterNotNull()
                ?.filter { it.nextAiringEpisode != null }
                ?.sortedBy { it.nextAiringEpisode?.timeUntilAiring }
            val pageInfo = response?.data?.Page?.pageInfo
            if (airingAnime != null) emit(PagedResult.Success(
                data = airingAnime,
                nextPage = if (pageInfo?.hasNextPage == true)
                    pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Empty"))
        }
    }

    fun getSeasonalAnimePage(
        animeSeason: AnimeSeason,
        page: Int = 1,
        perPage: Int = 15,
    ) = flow {
        emit(PagedResult.Loading)
        val response = SeasonalAnimeQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            season = Optional.present(animeSeason.season),
            seasonYear = Optional.present(animeSeason.year),
            sort = Optional.present(listOf(MediaSort.POPULARITY_DESC))
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val seasonalAnime = response?.data?.Page?.media?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (seasonalAnime != null) emit(PagedResult.Success(
                data = seasonalAnime,
                nextPage = if (pageInfo?.hasNextPage == true)
                    pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Empty"))
        }
    }

    fun getMediaSortedPage(
        mediaType: MediaType,
        sort: List<MediaSort>,
        page: Int = 1,
        perPage: Int = 15,
    ) = flow {
        emit(PagedResult.Loading)
        val response = MediaSortedQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            type = Optional.present(mediaType),
            sort = Optional.present(sort)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val media = response?.data?.Page?.media?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (media != null) emit(PagedResult.Success(
                data = media,
                nextPage = if (pageInfo?.hasNextPage == true)
                    pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Empty"))
        }
    }

    fun getMediaDetails(mediaId: Int) = flow {
        emit(DataResult.Loading)
        val response = MediaDetailsQuery(
            mediaId = Optional.present(mediaId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val media = response?.data?.Media
            if (media != null) emit(DataResult.Success(data = media))
            else emit(DataResult.Error(message = "Empty"))
        }
    }

    fun getMediaCharactersAndStaff(mediaId: Int) = flow {
        emit(DataResult.Loading)
        val response = MediaCharactersAndStaffQuery(
            mediaId = Optional.present(mediaId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val media = response?.data?.Media
            if (media != null) {
                val charactersAndStaff = MediaCharactersAndStaff(
                    characters = media.characters?.edges?.filterNotNull().orEmpty(),
                    staff = media.staff?.edges?.filterNotNull().orEmpty()
                )
                emit(DataResult.Success(data = charactersAndStaff))
            }
            else emit(DataResult.Error(message = "Empty"))
        }
    }

    fun getMediaRelationsRecommendations(mediaId: Int) = flow {
        emit(DataResult.Loading)
        val response = MediaRelationsAndRecommendationsQuery(
            mediaId = Optional.present(mediaId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val media = response?.data?.Media
            if (media != null) {
                val relationsAndRecommendations = MediaRelationsAndRecommendations(
                    relations = media.relations?.edges?.filterNotNull().orEmpty(),
                    recommendations = media.recommendations?.nodes?.filterNotNull().orEmpty()
                )
                emit(DataResult.Success(data = relationsAndRecommendations))
            }
            else emit(DataResult.Error(message = "Empty"))
        }
    }

    fun getMediaStats(mediaId: Int) = flow {
        emit(DataResult.Loading)
        val response = MediaStatsQuery(
            mediaId = Optional.present(mediaId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val media = response?.data?.Media
            if (media != null) {
                emit(DataResult.Success(data = media))
            }
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun getMediaReviewsPage(
        mediaId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)
        val response = MediaReviewsQuery(
            mediaId = Optional.present(mediaId),
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val reviews = response?.data?.Media?.reviews?.nodes?.filterNotNull()
            val pageInfo = response?.data?.Media?.reviews?.pageInfo
            if (reviews != null) {
                emit(PagedResult.Success(
                    data = reviews,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                ))
            }
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getMediaThreadsPage(
        mediaId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)
        val response = MediaThreadsQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            mediaCategoryId = Optional.present(mediaId),
            sort = Optional.present(listOf(ThreadSort.CREATED_AT_DESC))
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val threadsPage = response?.data?.Page
            if (threadsPage != null) {
                emit(PagedResult.Success(
                    data = threadsPage.threads?.filterNotNull().orEmpty(),
                    nextPage = if (threadsPage.pageInfo?.hasNextPage == true)
                            threadsPage.pageInfo.currentPage?.plus(1)
                    else null
                ))
            }
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getMediaChartPage(
        type: MediaType,
        sort: List<MediaSort> = listOf(MediaSort.ID),
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = MediaChartQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            sort = Optional.present(sort),
            type = Optional.present(type)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val media = response?.data?.Page?.media?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (media != null) emit(PagedResult.Success(
                data = media,
                nextPage = if (pageInfo?.hasNextPage == true)
                    pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    suspend fun getUserCurrentAiringAnime(userId: Int): List<UserCurrentAnimeListQuery.MediaList>? {
        val response = apolloClient.query(UserCurrentAnimeListQuery(
            userId = Optional.present(userId)
        )).execute()
        if (response.hasErrors()) return null
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