package com.axiel7.anihyou.feature.profile.favorites.reorder

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.model.FavoritesType
import com.axiel7.anihyou.core.ui.common.navigation.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.InjectedParam

@OptIn(ExperimentalCoroutinesApi::class)
class ReorderFavoritesViewModel(
    @InjectedParam arguments: Route.ReorderFavorites,
    private val favoritesRepository: FavoriteRepository
) : PagedUiStateViewModel<ReorderFavoritesUiState>(), ReorderFavoritesEvent {

    override val initialState = ReorderFavoritesUiState(
        userId = arguments.userId,
        type = arguments.type,
    )

    override fun onRefresh() {
        mutableUiState.update { it.copy(fetchFromNetwork = true, page = 1, hasNextPage = true) }
    }

    override fun onMove(from: Int, to: Int) {
        fun SnapshotStateList<*>.updateOrder() {
            add(to, removeAt(from))
        }

        with(uiState.value) {
            when (type) {
                FavoritesType.ANIME -> anime.updateOrder()
                FavoritesType.MANGA -> manga.updateOrder()
                FavoritesType.CHARACTERS -> characters.updateOrder()
                FavoritesType.STAFF -> staff.updateOrder()
                FavoritesType.STUDIOS -> studios.updateOrder()
            }
        }
    }

    override fun saveNewOrder() {
        with(uiState.value) {
            val animeIds = anime.map { it.id }.takeIf { type == FavoritesType.ANIME }
            val animeOrder = anime.indices.toList().takeIf { type == FavoritesType.ANIME }
            val mangaIds = manga.map { it.id }.takeIf { type == FavoritesType.MANGA }
            val mangaOrder = manga.indices.toList().takeIf { type == FavoritesType.MANGA }
            val characterIds = characters.map { it.id }.takeIf { type == FavoritesType.CHARACTERS }
            val characterOrder = characters.indices.toList().takeIf { type == FavoritesType.CHARACTERS }
            val staffIds = staff.map { it.id }.takeIf { type == FavoritesType.STAFF }
            val staffOrder = staff.indices.toList().takeIf { type == FavoritesType.STAFF }
            val studioIds = studios.map { it.id }.takeIf { type == FavoritesType.STUDIOS }
            val studioOrder = studios.indices.toList().takeIf { type == FavoritesType.STUDIOS }

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
    }


    init {
        // anime
        mutableUiState
            .filter {
                it.type == FavoritesType.ANIME
                        && it.hasNextPage
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                favoritesRepository.getFavoriteAnime(
                    userId = uiState.userId,
                    page = uiState.page,
                    perPage = 100,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                )
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
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                favoritesRepository.getFavoriteManga(
                    userId = uiState.userId,
                    page = uiState.page,
                    perPage = 100,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                )
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
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                favoritesRepository.getFavoriteCharacters(
                    userId = uiState.userId,
                    page = uiState.page,
                    perPage = 100,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                )
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
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                favoritesRepository.getFavoriteStaff(
                    userId = uiState.userId,
                    page = uiState.page,
                    perPage = 100,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                )
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
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                favoritesRepository.getFavoriteStudio(
                    userId = uiState.userId,
                    page = uiState.page,
                    perPage = 100,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                )
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
