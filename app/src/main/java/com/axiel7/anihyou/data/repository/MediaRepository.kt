package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.AiringOnMyListQuery
import com.axiel7.anihyou.MediaSortedQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.UserCurrentAnimeListQuery
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import com.axiel7.anihyou.network.apolloClient
import com.axiel7.anihyou.type.AiringSort
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.UiState
import kotlinx.coroutines.flow.flow

object MediaRepository {

    fun getAiringAnime(
        page: Int = 1,
        perPage: Int = 15,
    ) = flow {
        emit(UiState.Loading)
        val todayTimestamp = System.currentTimeMillis() / 1000
        val response = AiringAnimesQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            sort = Optional.present(listOf(AiringSort.TIME)),
            airingAtGreater = Optional.present(todayTimestamp.toInt())
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val airingAnime = response?.data?.Page?.airingSchedules?.filterNotNull()
            if (airingAnime != null) emit(UiState.Success(data = airingAnime))
            else emit(UiState.Error(message = "Empty"))
        }
    }

    fun getAiringAnimeOnMyList(
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(UiState.Loading)
        val response = AiringOnMyListQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val airingAnime = response?.data?.Page?.media?.filterNotNull()
                ?.filter { it.nextAiringEpisode != null }
                ?.sortedBy { it.nextAiringEpisode?.timeUntilAiring }
            if (airingAnime != null) emit(UiState.Success(data = airingAnime))
            else emit(UiState.Error(message = "Empty"))
        }
    }

    fun getSeasonalAnime(
        animeSeason: AnimeSeason,
        page: Int = 1,
        perPage: Int = 15,
    ) = flow {
        emit(UiState.Loading)
        val response = SeasonalAnimeQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            season = Optional.present(animeSeason.season),
            seasonYear = Optional.present(animeSeason.year),
            sort = Optional.present(listOf(MediaSort.POPULARITY_DESC))
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val seasonalAnime = response?.data?.Page?.media?.filterNotNull()
            if (seasonalAnime != null) emit(UiState.Success(data = seasonalAnime))
            else emit(UiState.Error(message = "Empty"))
        }
    }

    fun getMediaSorted(
        mediaType: MediaType,
        sort: List<MediaSort>,
        page: Int = 1,
        perPage: Int = 15,
    ) = flow {
        emit(UiState.Loading)
        val response = MediaSortedQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            type = Optional.present(mediaType),
            sort = Optional.present(sort)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val media = response?.data?.Page?.media?.filterNotNull()
            if (media != null) emit(UiState.Success(data = media))
            else emit(UiState.Error(message = "Empty"))
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