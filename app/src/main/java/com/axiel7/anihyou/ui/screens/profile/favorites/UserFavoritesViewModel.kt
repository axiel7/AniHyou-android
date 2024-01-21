package com.axiel7.anihyou.ui.screens.profile.favorites

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.UserFavoritesAnimeQuery
import com.axiel7.anihyou.UserFavoritesCharacterQuery
import com.axiel7.anihyou.UserFavoritesMangaQuery
import com.axiel7.anihyou.UserFavoritesStaffQuery
import com.axiel7.anihyou.UserFavoritesStudioQuery
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserFavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : PagedUiStateViewModel<UserFavoritesUiState>(), UserFavoritesEvent {

    override val initialState = UserFavoritesUiState()

    fun setUserId(value: Int) = mutableUiState.update { it.copy(userId = value) }

    override fun setType(value: FavoritesType) {
        mutableUiState.update {
            it.copy(type = value, page = 1, hasNextPage = true)
        }
    }

    val anime = mutableStateListOf<UserFavoritesAnimeQuery.Node>()
    val manga = mutableStateListOf<UserFavoritesMangaQuery.Node>()
    val characters = mutableStateListOf<UserFavoritesCharacterQuery.Node>()
    val staff = mutableStateListOf<UserFavoritesStaffQuery.Node>()
    var studios = mutableStateListOf<UserFavoritesStudioQuery.Node>()

    init {
        // anime
        mutableUiState
            .filter {
                it.type == FavoritesType.ANIME
                        && it.hasNextPage
                        && it.userId != null
            }
            .distinctUntilChangedBy { it.page }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    favoriteRepository.getFavoriteAnime(
                        userId = uiState.userId,
                        page = uiState.page
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        anime.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        // manga
        mutableUiState
            .filter {
                it.type == FavoritesType.MANGA
                        && it.hasNextPage
                        && it.userId != null
            }
            .distinctUntilChangedBy { it.page }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    favoriteRepository.getFavoriteManga(
                        userId = uiState.userId,
                        page = uiState.page
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        manga.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        // characters
        mutableUiState
            .filter {
                it.type == FavoritesType.CHARACTERS
                        && it.hasNextPage
                        && it.userId != null
            }
            .distinctUntilChangedBy { it.page }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    favoriteRepository.getFavoriteCharacters(
                        userId = uiState.userId,
                        page = uiState.page
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        characters.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        // staff
        mutableUiState
            .filter {
                it.type == FavoritesType.STAFF
                        && it.hasNextPage
                        && it.userId != null
            }
            .distinctUntilChangedBy { it.page }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    favoriteRepository.getFavoriteStaff(
                        userId = uiState.userId,
                        page = uiState.page
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        staff.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        // studios
        mutableUiState
            .filter {
                it.type == FavoritesType.STUDIOS
                        && it.hasNextPage
                        && it.userId != null
            }
            .distinctUntilChangedBy { it.page }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    favoriteRepository.getFavoriteStudio(
                        userId = uiState.userId,
                        page = uiState.page
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        studios.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}