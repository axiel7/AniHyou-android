package com.axiel7.anihyou.feature.profile.favorites

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.common.PagedResult
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.ui.common.viewmodel.PagedUiStateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class UserFavoritesViewModel(
    private val favoriteRepository: FavoriteRepository
) : PagedUiStateViewModel<UserFavoritesUiState>(), UserFavoritesEvent {

    override val initialState = UserFavoritesUiState()

    fun setUserId(value: Int) = mutableUiState.update { it.copy(userId = value) }

    override fun setType(value: FavoritesType) {
        mutableUiState.update {
            it.copy(type = value, page = 1, hasNextPage = true)
        }
    }

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
                        it.anime.addAll(result.list)
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
                        it.manga.addAll(result.list)
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
                        it.characters.addAll(result.list)
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
                        it.staff.addAll(result.list)
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
                        it.studios.addAll(result.list)
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