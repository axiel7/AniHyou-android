package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.FavoriteApi
import javax.inject.Inject

class FavoriteRepository @Inject constructor(
    private val api: FavoriteApi
) {

    fun toggleFavorite(
        animeId: Int? = null,
        mangaId: Int? = null,
        characterId: Int? = null,
        staffId: Int? = null,
        studioId: Int? = null,
    ) = api
        .toggleFavouriteMutation(animeId, mangaId, characterId, staffId, studioId)
        .toFlow()
        .asDataResult {
            it.ToggleFavourite
        }

    fun getFavoriteAnime(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = api
        .userFavoritesAnimeQuery(userId, page, perPage)
        .watch()
        .asDataResult {
            //TODO: use pagination?
            it.User?.favourites?.anime?.nodes?.filterNotNull()
        }

    fun getFavoriteManga(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = api
        .userFavoritesMangaQuery(userId, page, perPage)
        .watch()
        .asDataResult {
            //TODO: use pagination?
            it.User?.favourites?.manga?.nodes?.filterNotNull()
        }

    fun getFavoriteCharacters(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = api
        .userFavoritesCharacterQuery(userId, page, perPage)
        .watch()
        .asDataResult {
            //TODO: use pagination?
            it.User?.favourites?.characters?.nodes?.filterNotNull()
        }

    fun getFavoriteStaff(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = api
        .userFavoritesStaffQuery(userId, page, perPage)
        .watch()
        .asDataResult {
            //TODO: use pagination?
            it.User?.favourites?.staff?.nodes?.filterNotNull()
        }

    fun getFavoriteStudio(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = api
        .userFavoritesStudioQuery(userId, page, perPage)
        .watch()
        .asDataResult {
            //TODO: use pagination?
            it.User?.favourites?.studios?.nodes?.filterNotNull()
        }
}