package com.axiel7.anihyou.ui.screens.profile.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.UserFavoritesAnimeQuery
import com.axiel7.anihyou.UserFavoritesCharacterQuery
import com.axiel7.anihyou.UserFavoritesMangaQuery
import com.axiel7.anihyou.UserFavoritesStaffQuery
import com.axiel7.anihyou.UserFavoritesStudioQuery
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class UserFavoritesViewModel : BaseViewModel() {

    var favoritesType by mutableStateOf(FavoritesType.ANIME)

    suspend fun onFavoriteTypeChanged(userId: Int) {
        viewModelScope.launch {
            when (favoritesType) {
                FavoritesType.ANIME -> if (hasNextPageAnime) getAnime(userId)
                FavoritesType.MANGA -> if (hasNextPageManga) getManga(userId)
                FavoritesType.CHARACTERS -> if (hasNextPageCharacter) getCharacters(userId)
                FavoritesType.STAFF -> if (hasNextPageStaff) getStaff(userId)
                FavoritesType.STUDIOS -> if (hasNextPageStudio) getStudios(userId)
            }
        }
    }

    private var pageAnime = 1
    private var hasNextPageAnime = true
    var anime = mutableStateListOf<UserFavoritesAnimeQuery.Node>()

    private suspend fun getAnime(userId: Int) {
        isLoading = true
        val response = UserFavoritesAnimeQuery(
            userId = Optional.present(userId),
            page = Optional.present(pageAnime),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.User?.favourites?.anime?.nodes?.filterNotNull()?.let { anime.addAll(it) }
        hasNextPageAnime = response?.data?.User?.favourites?.anime?.pageInfo?.hasNextPage ?: false
        pageAnime = response?.data?.User?.favourites?.anime?.pageInfo?.currentPage?.plus(1) ?: pageAnime

        isLoading = false
    }

    private var pageManga = 1
    private var hasNextPageManga = true
    var manga = mutableStateListOf<UserFavoritesMangaQuery.Node>()

    private suspend fun getManga(userId: Int) {
        isLoading = true
        val response = UserFavoritesMangaQuery(
            userId = Optional.present(userId),
            page = Optional.present(pageManga),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.User?.favourites?.manga?.nodes?.filterNotNull()?.let { manga.addAll(it) }
        hasNextPageManga = response?.data?.User?.favourites?.manga?.pageInfo?.hasNextPage ?: false
        pageManga = response?.data?.User?.favourites?.manga?.pageInfo?.currentPage?.plus(1) ?: pageManga

        isLoading = false
    }

    private var pageCharacter = 1
    private var hasNextPageCharacter = true
    var characters = mutableStateListOf<UserFavoritesCharacterQuery.Node>()

    private suspend fun getCharacters(userId: Int) {
        isLoading = true
        val response = UserFavoritesCharacterQuery(
            userId = Optional.present(userId),
            page = Optional.present(pageCharacter),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.User?.favourites?.characters?.nodes?.filterNotNull()?.let { characters.addAll(it) }
        hasNextPageCharacter = response?.data?.User?.favourites?.characters?.pageInfo?.hasNextPage ?: false
        pageCharacter = response?.data?.User?.favourites?.characters?.pageInfo?.currentPage?.plus(1) ?: pageCharacter

        isLoading = false
    }

    private var pageStaff = 1
    private var hasNextPageStaff = true
    var staff = mutableStateListOf<UserFavoritesStaffQuery.Node>()

    private suspend fun getStaff(userId: Int) {
        isLoading = true
        val response = UserFavoritesStaffQuery(
            userId = Optional.present(userId),
            page = Optional.present(pageStaff),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.User?.favourites?.staff?.nodes?.filterNotNull()?.let { staff.addAll(it) }
        hasNextPageStaff = response?.data?.User?.favourites?.staff?.pageInfo?.hasNextPage ?: false
        pageStaff = response?.data?.User?.favourites?.staff?.pageInfo?.currentPage?.plus(1) ?: pageStaff

        isLoading = false
    }

    private var pageStudio = 1
    private var hasNextPageStudio = true
    var studios = mutableStateListOf<UserFavoritesStudioQuery.Node>()

    private suspend fun getStudios(userId: Int) {
        isLoading = true
        val response = UserFavoritesStudioQuery(
            userId = Optional.present(userId),
            page = Optional.present(pageStudio),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.User?.favourites?.studios?.nodes?.filterNotNull()?.let { studios.addAll(it) }
        hasNextPageStudio = response?.data?.User?.favourites?.studios?.pageInfo?.hasNextPage ?: false
        pageStudio = response?.data?.User?.favourites?.studios?.pageInfo?.currentPage?.plus(1) ?: pageStudio

        isLoading = false
    }
}