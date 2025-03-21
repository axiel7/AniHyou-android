package com.axiel7.anihyou.core.domain.repository

import com.apollographql.apollo.cache.normalized.watch
import com.axiel7.anihyou.core.network.api.FavoriteApi

class FavoriteRepository(
    private val api: FavoriteApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

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
        page: Int,
        perPage: Int = 25,
    ) = api
        .userFavoritesAnimeQuery(userId, page, perPage)
        .watch()
        .asPagedResult(page = { it.User?.favourites?.anime?.pageInfo?.commonPage }) {
            it.User?.favourites?.anime?.nodes?.filterNotNull().orEmpty()
        }

    fun getFavoriteManga(
        userId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .userFavoritesMangaQuery(userId, page, perPage)
        .watch()
        .asPagedResult(page = { it.User?.favourites?.manga?.pageInfo?.commonPage }) {
            it.User?.favourites?.manga?.nodes?.filterNotNull().orEmpty()
        }

    fun getFavoriteCharacters(
        userId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .userFavoritesCharacterQuery(userId, page, perPage)
        .watch()
        .asPagedResult(page = { it.User?.favourites?.characters?.pageInfo?.commonPage }) {
            it.User?.favourites?.characters?.nodes?.filterNotNull().orEmpty()
        }

    fun getFavoriteStaff(
        userId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .userFavoritesStaffQuery(userId, page, perPage)
        .watch()
        .asPagedResult(page = { it.User?.favourites?.staff?.pageInfo?.commonPage }) {
            it.User?.favourites?.staff?.nodes?.filterNotNull().orEmpty()
        }

    fun getFavoriteStudio(
        userId: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .userFavoritesStudioQuery(userId, page, perPage)
        .watch()
        .asPagedResult(page = { it.User?.favourites?.studios?.pageInfo?.commonPage }) {
            it.User?.favourites?.studios?.nodes?.filterNotNull().orEmpty()
        }
}