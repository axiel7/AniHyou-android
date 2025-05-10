package com.axiel7.anihyou.core.network.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.axiel7.anihyou.core.network.ToggleFavouriteMutation
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
}