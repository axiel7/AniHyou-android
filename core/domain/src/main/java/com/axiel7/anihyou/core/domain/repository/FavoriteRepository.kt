package com.axiel7.anihyou.core.domain.repository

import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.network.api.FavoriteApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FavoriteRepository(
    private val api: FavoriteApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    private val _favoriteToggled = MutableSharedFlow<Boolean>(replay = 1)
    val favoriteToggled = _favoriteToggled.asSharedFlow()

    suspend fun toggleFavorite(
        animeId: Int? = null,
        mangaId: Int? = null,
        characterId: Int? = null,
        staffId: Int? = null,
        studioId: Int? = null,
    ) = api
        .toggleFavouriteMutation(animeId, mangaId, characterId, staffId, studioId)
        .execute()
        .asDataResult {
            it.ToggleFavourite
        }.also {
            if (it is DataResult.Success) _favoriteToggled.emit(true)
        }

    fun getFavoriteAnime(
        userId: Int,
        page: Int,
        perPage: Int = 25,
        fetchFromNetwork: Boolean,
    ) = api
        .userFavoritesAnimeQuery(userId, page, perPage)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asPagedResult(page = { it.User?.favourites?.anime?.pageInfo?.commonPage }) {
            it.User?.favourites?.anime?.nodes?.filterNotNull().orEmpty()
        }

    fun getFavoriteManga(
        userId: Int,
        page: Int,
        perPage: Int = 25,
        fetchFromNetwork: Boolean,
    ) = api
        .userFavoritesMangaQuery(userId, page, perPage)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asPagedResult(page = { it.User?.favourites?.manga?.pageInfo?.commonPage }) {
            it.User?.favourites?.manga?.nodes?.filterNotNull().orEmpty()
        }

    fun getFavoriteCharacters(
        userId: Int,
        page: Int,
        perPage: Int = 25,
        fetchFromNetwork: Boolean,
    ) = api
        .userFavoritesCharacterQuery(userId, page, perPage)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asPagedResult(page = { it.User?.favourites?.characters?.pageInfo?.commonPage }) {
            it.User?.favourites?.characters?.nodes?.filterNotNull().orEmpty()
        }

    fun getFavoriteStaff(
        userId: Int,
        page: Int,
        perPage: Int = 25,
        fetchFromNetwork: Boolean,
    ) = api
        .userFavoritesStaffQuery(userId, page, perPage)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asPagedResult(page = { it.User?.favourites?.staff?.pageInfo?.commonPage }) {
            it.User?.favourites?.staff?.nodes?.filterNotNull().orEmpty()
        }

    fun getFavoriteStudio(
        userId: Int,
        page: Int,
        perPage: Int = 25,
        fetchFromNetwork: Boolean,
    ) = api
        .userFavoritesStudioQuery(userId, page, perPage)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asPagedResult(page = { it.User?.favourites?.studios?.pageInfo?.commonPage }) {
            it.User?.favourites?.studios?.nodes?.filterNotNull().orEmpty()
        }

    fun updateFavouriteOrder(
        animeIds: List<Int>? = null,
        animeOrder: List<Int>? = null,
        mangaIds: List<Int>? = null,
        mangaOrder: List<Int>? = null,
        characterIds: List<Int>? = null,
        characterOrder: List<Int>? = null,
        staffIds: List<Int>? = null,
        staffOrder: List<Int>? = null,
        studioIds: List<Int>? = null,
        studioOrder: List<Int>? = null
    ) = api
        .updateFavouriteOrderMutation(
            animeIds,
            animeOrder,
            mangaIds,
            mangaOrder,
            characterIds,
            characterOrder,
            staffIds,
            staffOrder,
            studioIds,
            studioOrder
        )
        .toFlow()
        .asDataResult()
}