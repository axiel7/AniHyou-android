package com.axiel7.anihyou.feature.profile.favorites

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.model.FavoritesType
import com.axiel7.anihyou.core.network.UserFavoritesAnimeQuery
import com.axiel7.anihyou.core.network.UserFavoritesCharacterQuery
import com.axiel7.anihyou.core.network.UserFavoritesMangaQuery
import com.axiel7.anihyou.core.network.UserFavoritesStaffQuery
import com.axiel7.anihyou.core.network.UserFavoritesStudioQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
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

    override fun updateAfterReorderSaved(result: List<*>) {
        mutableUiState.update { currentState ->
            when (uiState.value.type) {
                FavoritesType.ANIME -> {
                    currentState.anime.clear()
                    currentState.anime.addAll(result.filterIsInstance<UserFavoritesAnimeQuery.Node>())
                }
                FavoritesType.MANGA -> {
                    currentState.manga.clear()
                    currentState.manga.addAll(result.filterIsInstance<UserFavoritesMangaQuery.Node>())
                }
                FavoritesType.CHARACTERS -> {
                    currentState.characters.clear()
                    currentState.characters.addAll(result.filterIsInstance<UserFavoritesCharacterQuery.Node>())
                }
                FavoritesType.STAFF -> {
                    currentState.staff.clear()
                    currentState.staff.addAll(result.filterIsInstance<UserFavoritesStaffQuery.Node>())
                }
                FavoritesType.STUDIOS -> {
                    currentState.studios.clear()
                    currentState.studios.addAll(result.filterIsInstance<UserFavoritesStudioQuery.Node>())
                }
            }
            currentState
        }
    }

    override fun onRefresh() {
        mutableUiState.update { it.copy(fetchFromNetwork = true, page = 1, hasNextPage = true) }
    }

    init {
        // anime
        mutableUiState
            .filter {
                it.type == FavoritesType.ANIME
                        && it.hasNextPage
                        && it.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    favoriteRepository.getFavoriteAnime(
                        userId = uiState.userId,
                        page = uiState.page,
                        fetchFromNetwork = uiState.fetchFromNetwork,
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.anime.clear()
                        it.anime.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
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
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    favoriteRepository.getFavoriteManga(
                        userId = uiState.userId,
                        page = uiState.page,
                        fetchFromNetwork = uiState.fetchFromNetwork,
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.manga.clear()
                        it.manga.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
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
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    favoriteRepository.getFavoriteCharacters(
                        userId = uiState.userId,
                        page = uiState.page,
                        fetchFromNetwork = uiState.fetchFromNetwork,
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.characters.clear()
                        it.characters.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
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
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    favoriteRepository.getFavoriteStaff(
                        userId = uiState.userId,
                        page = uiState.page,
                        fetchFromNetwork = uiState.fetchFromNetwork,
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.staff.clear()
                        it.staff.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
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
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    favoriteRepository.getFavoriteStudio(
                        userId = uiState.userId,
                        page = uiState.page,
                        fetchFromNetwork = uiState.fetchFromNetwork,
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.studios.clear()
                        it.studios.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}