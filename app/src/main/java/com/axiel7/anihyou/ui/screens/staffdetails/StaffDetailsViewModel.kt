package com.axiel7.anihyou.ui.screens.staffdetails

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.StaffCharacterQuery
import com.axiel7.anihyou.StaffMediaQuery
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.data.repository.StaffRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.common.navigation.NavArgument
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StaffDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val staffRepository: StaffRepository,
    private val favoriteRepository: FavoriteRepository,
) : UiStateViewModel<StaffDetailsUiState>(), StaffDetailsEvent {

    private val staffId = savedStateHandle.getStateFlow<Int?>(NavArgument.StaffId.name, null)

    override val mutableUiState = MutableStateFlow(StaffDetailsUiState())
    override val uiState = mutableUiState.asStateFlow()

    override fun setMediaOnMyList(value: Boolean?) {
        mutableUiState.update {
            it.copy(mediaOnMyList = value, pageMedia = 1, hasNextPageMedia = true)
        }
    }

    override fun toggleFavorite() {
        staffId.value?.let { staffId ->
            favoriteRepository.toggleFavorite(staffId = staffId)
                .onEach { result ->
                    if (result is DataResult.Success && result.data != null) {
                        mutableUiState.update { uiState ->
                            uiState.copy(
                                isLoading = false,
                                details = uiState.details?.copy(
                                    isFavourite = !uiState.details.isFavourite
                                )
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    override fun loadNextPageMedia() {
        if (mutableUiState.value.hasNextPageMedia)
            mutableUiState.update { it.copy(pageMedia = it.pageMedia + 1) }
    }

    override fun selectMediaItem(value: Pair<Int, StaffMediaGrouped>?) = mutableUiState.update {
        it.copy(selectedMediaItem = value)
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        uiState.value.selectedMediaItem?.let { selectedItem ->
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

    override fun loadNextPageCharacters() {
        if (mutableUiState.value.hasNextPageCharacters)
            mutableUiState.update { it.copy(pageCharacters = it.pageCharacters + 1) }
    }

    val media = mutableStateListOf<Pair<Int, StaffMediaGrouped>>()
    val characters = mutableStateListOf<StaffCharacterQuery.Edge>()

    init {
        // staff details
        staffId
            .filterNotNull()
            .flatMapLatest { staffId ->
                staffRepository.getStaffDetails(staffId)
            }
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
            .combine(staffId.filterNotNull(), ::Pair)
            .flatMapLatest { (uiState, staffId) ->
                staffRepository.getStaffMediaPage(
                    staffId = staffId,
                    onList = uiState.mediaOnMyList,
                    page = uiState.pageMedia
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.pageMedia == 1) media.clear()
                        media.addAll(result.list)
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
            .distinctUntilChangedBy { it.pageCharacters }
            .combine(staffId.filterNotNull(), ::Pair)
            .flatMapLatest { (uiState, staffId) ->
                staffRepository.getStaffCharactersPage(
                    staffId = staffId,
                    page = uiState.pageCharacters
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        characters.addAll(result.list)
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