package com.axiel7.anihyou.feature.profile.favorites

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.model.FavoritesType
import com.axiel7.anihyou.core.network.UserFavoritesAnimeQuery
import com.axiel7.anihyou.core.network.UserFavoritesCharacterQuery
import com.axiel7.anihyou.core.network.UserFavoritesMangaQuery
import com.axiel7.anihyou.core.network.UserFavoritesStaffQuery
import com.axiel7.anihyou.core.network.UserFavoritesStudioQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.InjectedParam

@OptIn(ExperimentalCoroutinesApi::class)
class UserFavoritesViewModel(
    @InjectedParam userId: Int,
    @InjectedParam isMyProfile: Boolean,
    private val favoriteRepository: FavoriteRepository
) : PagedUiStateViewModel<UserFavoritesUiState>(), UserFavoritesEvent {

    override val initialState = UserFavoritesUiState(userId = userId, isMyProfile = isMyProfile)

    override fun setType(value: FavoritesType) {
        mutableUiState.update {
            it.copy(type = value, page = 1, hasNextPage = true)
        }
    }

    override fun updateAfterReorderSaved(result: List<*>) {
        with(uiState.value) {
            when (type) {
                FavoritesType.ANIME -> {
                    anime.clear()
                    anime.addAll(result.filterIsInstance<UserFavoritesAnimeQuery.Node>())
                }
                FavoritesType.MANGA -> {
                    manga.clear()
                    manga.addAll(result.filterIsInstance<UserFavoritesMangaQuery.Node>())
                }
                FavoritesType.CHARACTERS -> {
                    characters.clear()
                    characters.addAll(result.filterIsInstance<UserFavoritesCharacterQuery.Node>())
                }
                FavoritesType.STAFF -> {
                    staff.clear()
                    staff.addAll(result.filterIsInstance<UserFavoritesStaffQuery.Node>())
                }
                FavoritesType.STUDIOS -> {
                    studios.clear()
                    studios.addAll(result.filterIsInstance<UserFavoritesStudioQuery.Node>())
                }
            }
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
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                favoriteRepository.getFavoriteAnime(
                    userId = uiState.userId,
                    page = uiState.page,
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
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                favoriteRepository.getFavoriteManga(
                    userId = uiState.userId,
                    page = uiState.page,
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
                favoriteRepository.getFavoriteCharacters(
                    userId = uiState.userId,
                    page = uiState.page,
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
                favoriteRepository.getFavoriteStaff(
                    userId = uiState.userId,
                    page = uiState.page,
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
                favoriteRepository.getFavoriteStudio(
                    userId = uiState.userId,
                    page = uiState.page,
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