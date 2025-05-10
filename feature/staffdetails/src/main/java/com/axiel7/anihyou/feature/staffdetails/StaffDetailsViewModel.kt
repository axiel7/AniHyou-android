package com.axiel7.anihyou.feature.staffdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.domain.repository.StaffRepository
import com.axiel7.anihyou.core.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.core.network.StaffMediaQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class StaffDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val staffRepository: StaffRepository,
    private val favoriteRepository: FavoriteRepository,
) : UiStateViewModel<StaffDetailsUiState>(), StaffDetailsEvent {

    private val arguments = savedStateHandle.toRoute<Routes.StaffDetails>()

    override val initialState = StaffDetailsUiState()

    override fun setMediaOnMyList(value: Boolean?) {
        mutableUiState.update {
            it.copy(mediaOnMyList = value, pageMedia = 1, hasNextPageMedia = true)
        }
    }

    override fun toggleFavorite() {
        favoriteRepository.toggleFavorite(staffId = arguments.id)
            .onEach { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.update { uiState ->
                        val newDetails = uiState.details
                            ?.copy(isFavourite = !uiState.details.isFavourite)
                            ?.also {
                                staffRepository.updateStaffDetailsCache(it)
                            }
                        uiState.copy(
                            isLoading = false,
                            details = newDetails
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun loadNextPageMedia() {
        if (mutableUiState.value.hasNextPageMedia)
            mutableUiState.update { it.copy(pageMedia = it.pageMedia + 1) }
    }

    override fun selectMediaItem(value: Pair<Int, StaffMediaGrouped>?) = mutableUiState.update {
        it.copy(selectedMediaItem = value)
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        uiState.value.run {
            selectedMediaItem?.let { selectedItem ->
                val index = media.indexOfFirst { it.first == selectedItem.first }
                if (index != -1) {
                    media[index] = selectedItem.copy(
                        first = selectedItem.first,
                        second = selectedItem.second.copy(
                            value = selectedItem.second.value.copy(
                                node = selectedItem.second.value.node?.copy(
                                    mediaListEntry = newListEntry?.let {
                                        StaffMediaQuery.MediaListEntry(
                                            __typename = "StaffMediaQuery.MediaListEntry",
                                            id = newListEntry.id,
                                            mediaId = newListEntry.mediaId,
                                            basicMediaListEntry = newListEntry
                                        )
                                    }
                                )
                            )
                        )
                    )
                }
            }
        }
    }

    override fun loadNextPageCharacters() {
        if (mutableUiState.value.hasNextPageCharacters)
            mutableUiState.update { it.copy(pageCharacters = it.pageCharacters + 1) }
    }

    override fun setCharactersOnMyList(value: Boolean?) {
        mutableUiState.update {
            it.copy(
                charactersOnMyList = value,
                pageCharacters = 1,
                hasNextPageCharacters = true,
            )
        }
    }

    init {
        staffRepository.getStaffDetails(arguments.id)
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            isLoading = false,
                            details = result.data
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)

        // staff media
        mutableUiState
            .filter { it.hasNextPageMedia }
            .distinctUntilChanged { old, new ->
                old.pageMedia == new.pageMedia
                        && old.mediaOnMyList == new.mediaOnMyList
            }
            .flatMapLatest { uiState ->
                staffRepository.getStaffMediaPage(
                    staffId = arguments.id,
                    onList = uiState.mediaOnMyList,
                    page = uiState.pageMedia
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.pageMedia == 1) it.media.clear()
                        it.media.addAll(result.list)
                        it.copy(
                            hasNextPageMedia = result.hasNextPage,
                            isLoadingMedia = false,
                        )
                    } else {
                        it.copy(
                            isLoadingMedia = result is PagedResult.Loading && it.pageMedia == 1
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

        // staff characters
        mutableUiState
            .filter { it.hasNextPageCharacters }
            .distinctUntilChanged { old, new ->
                old.pageCharacters == new.pageCharacters
                        && old.charactersOnMyList == new.charactersOnMyList
            }
            .flatMapLatest { uiState ->
                staffRepository.getStaffCharactersPage(
                    staffId = arguments.id,
                    onList = uiState.charactersOnMyList,
                    page = uiState.pageCharacters
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.pageCharacters == 1) it.characters.clear()
                        it.characters.addAll(result.list)
                        it.copy(
                            hasNextPageCharacters = result.hasNextPage,
                            isLoadingCharacters = false
                        )
                    } else {
                        it.copy(
                            isLoadingCharacters = result is PagedResult.Loading
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}