package com.axiel7.anihyou.core.network.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.axiel7.anihyou.core.network.ToggleFavouriteMutation
import com.axiel7.anihyou.core.network.UpdateFavouriteOrderMutation
import com.axiel7.anihyou.core.network.UserFavoritesAnimeQuery
import com.axiel7.anihyou.core.network.UserFavoritesCharacterQuery
import com.axiel7.anihyou.core.network.UserFavoritesMangaQuery
import com.axiel7.anihyou.core.network.UserFavoritesStaffQuery
import com.axiel7.anihyou.core.network.UserFavoritesStudioQuery

class FavoriteApi(
    private val client: ApolloClient
) {
    fun toggleFavouriteMutation(
        animeId: Int?,
        mangaId: Int?,
        characterId: Int?,
        staffId: Int?,
        studioId: Int?,
    ) = client
        .mutation(
            ToggleFavouriteMutation(
                animeId = Optional.presentIfNotNull(animeId),
                mangaId = Optional.presentIfNotNull(mangaId),
                characterId = Optional.presentIfNotNull(characterId),
                staffId = Optional.presentIfNotNull(staffId),
                studioId = Optional.presentIfNotNull(studioId),
            )
        )

    fun userFavoritesAnimeQuery(
        userId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            UserFavoritesAnimeQuery(
                userId = Optional.present(userId),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun userFavoritesMangaQuery(
        userId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            UserFavoritesMangaQuery(
                userId = Optional.present(userId),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun userFavoritesCharacterQuery(
        userId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            UserFavoritesCharacterQuery(
                userId = Optional.present(userId),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun userFavoritesStaffQuery(
        userId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            UserFavoritesStaffQuery(
                userId = Optional.present(userId),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun userFavoritesStudioQuery(
        userId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            UserFavoritesStudioQuery(
                userId = Optional.present(userId),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun updateFavouriteOrderMutation(
        animeIds: List<Int>?,
        animeOrder: List<Int>?,
        mangaIds: List<Int>?,
        mangaOrder: List<Int>?,
        characterIds: List<Int>?,
        characterOrder: List<Int>?,
        staffIds: List<Int>?,
        staffOrder: List<Int>?,
        studioIds: List<Int>?,
        studioOrder: List<Int>?,
    ) = client
        .mutation(
            UpdateFavouriteOrderMutation(
                animeIds = Optional.presentIfNotNull(animeIds),
                animeOrder = Optional.presentIfNotNull(animeOrder),
                mangaIds = Optional.presentIfNotNull(mangaIds),
                mangaOrder = Optional.presentIfNotNull(mangaOrder),
                characterIds = Optional.presentIfNotNull(characterIds),
                characterOrder = Optional.presentIfNotNull(characterOrder),
                staffIds = Optional.presentIfNotNull(staffIds),
                staffOrder = Optional.presentIfNotNull(staffOrder),
                studioIds = Optional.presentIfNotNull(studioIds),
                studioOrder = Optional.presentIfNotNull(studioOrder),
            )
        )
}