package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ToggleFavouriteMutation
import com.axiel7.anihyou.UserFavoritesAnimeQuery
import com.axiel7.anihyou.UserFavoritesCharacterQuery
import com.axiel7.anihyou.UserFavoritesMangaQuery
import com.axiel7.anihyou.UserFavoritesStaffQuery
import com.axiel7.anihyou.UserFavoritesStudioQuery
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryMutation
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import kotlinx.coroutines.flow.flow

object FavoriteRepository {

    fun toggleFavorite(
        animeId: Int? = null,
        mangaId: Int? = null,
        characterId: Int? = null,
        staffId: Int? = null,
        studioId: Int? = null,
    ) = flow {
        emit(DataResult.Loading)
        val response = ToggleFavouriteMutation(
            animeId = Optional.presentIfNotNull(animeId),
            mangaId = Optional.presentIfNotNull(mangaId),
            characterId = Optional.presentIfNotNull(characterId),
            staffId = Optional.presentIfNotNull(staffId),
            studioId = Optional.presentIfNotNull(studioId),
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val favorite = response?.data?.ToggleFavourite
            if (favorite != null) emit(DataResult.Success(true))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun getFavoriteAnime(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = UserFavoritesAnimeQuery(
            userId = Optional.present(userId),
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val anime = response?.data?.User?.favourites?.anime?.nodes?.filterNotNull()
            val pageInfo = response?.data?.User?.favourites?.anime?.pageInfo
            if (anime != null) emit(
                PagedResult.Success(
                    data = anime,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getFavoriteManga(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = UserFavoritesMangaQuery(
            userId = Optional.present(userId),
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val manga = response?.data?.User?.favourites?.manga?.nodes?.filterNotNull()
            val pageInfo = response?.data?.User?.favourites?.manga?.pageInfo
            if (manga != null) emit(
                PagedResult.Success(
                    data = manga,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getFavoriteCharacters(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = UserFavoritesCharacterQuery(
            userId = Optional.present(userId),
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val characters = response?.data?.User?.favourites?.characters?.nodes?.filterNotNull()
            val pageInfo = response?.data?.User?.favourites?.characters?.pageInfo
            if (characters != null) emit(
                PagedResult.Success(
                    data = characters,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getFavoriteStaff(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = UserFavoritesStaffQuery(
            userId = Optional.present(userId),
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val staff = response?.data?.User?.favourites?.staff?.nodes?.filterNotNull()
            val pageInfo = response?.data?.User?.favourites?.staff?.pageInfo
            if (staff != null) emit(
                PagedResult.Success(
                    data = staff,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getFavoriteStudio(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = UserFavoritesStudioQuery(
            userId = Optional.present(userId),
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val studios = response?.data?.User?.favourites?.studios?.nodes?.filterNotNull()
            val pageInfo = response?.data?.User?.favourites?.studios?.pageInfo
            if (studios != null) emit(
                PagedResult.Success(
                    data = studios,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }
}