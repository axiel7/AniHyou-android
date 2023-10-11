package com.axiel7.anihyou.ui.screens.profile.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.UserFavoritesAnimeQuery
import com.axiel7.anihyou.UserFavoritesCharacterQuery
import com.axiel7.anihyou.UserFavoritesMangaQuery
import com.axiel7.anihyou.UserFavoritesStaffQuery
import com.axiel7.anihyou.UserFavoritesStudioQuery
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.ui.screens.profile.USER_ID_ARGUMENT
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserFavoritesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    //TODO: user favorites

    val userId: Int = savedStateHandle[USER_ID_ARGUMENT.removeFirstAndLast()]!!

    var isLoading by mutableStateOf(true)
        private set

    var favoritesType by mutableStateOf(FavoritesType.ANIME)
        private set

    fun onFavoriteTypeChanged(value: FavoritesType) {
        favoritesType = value
        when (favoritesType) {
            FavoritesType.ANIME -> if (hasNextPageAnime) getAnime(userId)
            FavoritesType.MANGA -> if (hasNextPageManga) getManga(userId)
            FavoritesType.CHARACTERS -> if (hasNextPageCharacter) getCharacters(userId)
            FavoritesType.STAFF -> if (hasNextPageStaff) getStaff(userId)
            FavoritesType.STUDIOS -> if (hasNextPageStudio) getStudios(userId)
        }
    }

    private var pageAnime = 1
    private var hasNextPageAnime = true
    var anime = mutableStateListOf<UserFavoritesAnimeQuery.Node>()

    private fun getAnime(userId: Int) = viewModelScope.launch {
        /*FavoriteRepository.getFavoriteAnime(
            userId = userId,
            page = pageAnime
        ).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                anime.addAll(result.data)
                hasNextPageAnime = result.nextPage != null
                pageAnime = result.nextPage ?: pageAnime
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }*/
    }

    private var pageManga = 1
    private var hasNextPageManga = true
    var manga = mutableStateListOf<UserFavoritesMangaQuery.Node>()

    private fun getManga(userId: Int) = viewModelScope.launch {
        /*FavoriteRepository.getFavoriteManga(
            userId = userId,
            page = pageManga
        ).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                manga.addAll(result.data)
                hasNextPageManga = result.nextPage != null
                pageManga = result.nextPage ?: pageManga
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }*/
    }

    private var pageCharacter = 1
    private var hasNextPageCharacter = true
    var characters = mutableStateListOf<UserFavoritesCharacterQuery.Node>()

    private fun getCharacters(userId: Int) = viewModelScope.launch {
        /*FavoriteRepository.getFavoriteCharacters(
            userId = userId,
            page = pageCharacter
        ).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                characters.addAll(result.data)
                hasNextPageCharacter = result.nextPage != null
                pageCharacter = result.nextPage ?: pageCharacter
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }*/
    }

    private var pageStaff = 1
    private var hasNextPageStaff = true
    var staff = mutableStateListOf<UserFavoritesStaffQuery.Node>()

    private fun getStaff(userId: Int) = viewModelScope.launch {
        /*FavoriteRepository.getFavoriteStaff(
            userId = userId,
            page = pageStaff
        ).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                staff.addAll(result.data)
                hasNextPageStaff = result.nextPage != null
                pageStaff = result.nextPage ?: pageStaff
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }*/
    }

    private var pageStudio = 1
    private var hasNextPageStudio = true
    var studios = mutableStateListOf<UserFavoritesStudioQuery.Node>()

    private fun getStudios(userId: Int) = viewModelScope.launch {
        /*FavoriteRepository.getFavoriteStudio(
            userId = userId,
            page = pageStudio
        ).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                studios.addAll(result.data)
                hasNextPageStudio = result.nextPage != null
                pageStudio = result.nextPage ?: pageStudio
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }*/
    }
}