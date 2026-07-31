package com.axiel7.anihyou.feature.profile.favorites.reorder

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.model.FavoritesType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
data class ReorderFavoritesViewModel(
    private val favoritesRepository: FavoriteRepository
) : PagedUiStateViewModel<ReorderFavoritesUiState>(), ReorderFavoritesEvent {
    override val initialState = ReorderFavoritesUiState()

    fun setUserId(value: Int?) = mutableUiState.update { it.copy(userId = value) }

    fun setType(value: FavoritesType) = mutableUiState.update { it.copy(type = value, page = 1, hasNextPage = true) }

    override fun onRefresh() {
        mutableUiState.update { it.copy(fetchFromNetwork = true, page = 1, hasNextPage = true) }
    }

    override fun saveNewOrder() {
        val currentState = uiState.value

        val animeIds = if (currentState.type == FavoritesType.ANIME) currentState.anime.map { it.id } else null
        val animeOrder = if (currentState.type == FavoritesType.ANIME) currentState.anime.indices.toList() else null
        val mangaIds = if (currentState.type == FavoritesType.MANGA) currentState.manga.map { it.id } else null
        val mangaOrder = if (currentState.type == FavoritesType.MANGA) currentState.manga.indices.toList() else null
        val characterIds = if (currentState.type == FavoritesType.CHARACTERS) currentState.characters.map { it.id } else null
        val characterOrder = if (currentState.type == FavoritesType.CHARACTERS) currentState.characters.indices.toList() else null
        val staffIds = if (currentState.type == FavoritesType.STAFF) currentState.staff.map { it.id } else null
        val staffOrder = if (currentState.type == FavoritesType.STAFF) currentState.staff.indices.toList() else null
        val studioIds = if (currentState.type == FavoritesType.STUDIOS) currentState.studios.map { it.id } else null
        val studioOrder = if (currentState.type == FavoritesType.STUDIOS) currentState.studios.indices.toList() else null

        favoritesRepository.updateFavouriteOrder(
            animeIds = animeIds, animeOrder = animeOrder,
            mangaIds = mangaIds, mangaOrder = mangaOrder,
            characterIds = characterIds, characterOrder = characterOrder,
            staffIds = staffIds, staffOrder = staffOrder,
            studioIds = studioIds, studioOrder = studioOrder
        ).onEach { result ->
            if (result is DataResult.Success) {
                mutableUiState.update { it.copy(error = null, isSaved = true) }
            } else if (result is DataResult.Error) {
                mutableUiState.update { it.copy(error = result.message, isSaved = false) }
            }
        }.launchIn(viewModelScope)
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
                    favoritesRepository.getFavoriteAnime(
                        userId = uiState.userId,
                        page = uiState.page,
                        perPage = 100,
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
                            fetchFromNetwork = false
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
                    favoritesRepository.getFavoriteManga(
                        userId = uiState.userId,
                        page = uiState.page,
                        perPage = 100,
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
                    favoritesRepository.getFavoriteCharacters(
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
                    favoritesRepository.getFavoriteStaff(
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
                    favoritesRepository.getFavoriteStudio(
                        userId = uiState.userId,
                        page = uiState.page,
                        perPage = 100,
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
