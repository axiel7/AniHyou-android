package com.axiel7.anihyou.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ToggleFavouriteMutation
import com.axiel7.anihyou.UserFavoritesAnimeQuery
import com.axiel7.anihyou.UserFavoritesCharacterQuery
import com.axiel7.anihyou.UserFavoritesMangaQuery
import com.axiel7.anihyou.UserFavoritesStaffQuery
import com.axiel7.anihyou.UserFavoritesStudioQuery
import javax.inject.Inject

class FavoriteApi @Inject constructor(
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