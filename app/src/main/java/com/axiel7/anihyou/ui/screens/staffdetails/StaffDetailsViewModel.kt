package com.axiel7.anihyou.ui.screens.staffdetails

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.StaffCharacterQuery
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.data.repository.StaffRepository
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
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
) : UiStateViewModel<StaffDetailsUiState>() {

    val staffId = savedStateHandle.getStateFlow<Int?>(STAFF_ID_ARGUMENT.removeFirstAndLast(), null)

    override val mutableUiState = MutableStateFlow(StaffDetailsUiState())
    override val uiState = mutableUiState.asStateFlow()

    fun setMediaOnMyList(value: Boolean) = mutableUiState.update {
        it.copy(mediaOnMyList = value, pageMedia = 1, hasNextPageMedia = true)
    }

    fun toggleFavorite() {
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

    val media = mutableStateListOf<Pair<Int, StaffMediaGrouped>>()

    fun loadNextPageMedia() {
        if (mutableUiState.value.hasNextPageMedia)
            mutableUiState.update { it.copy(pageMedia = it.pageMedia + 1) }
    }

    val characters = mutableStateListOf<StaffCharacterQuery.Edge>()

    fun loadNextPageCharacters() {
        if (mutableUiState.value.hasNextPageCharacters)
            mutableUiState.update { it.copy(pageCharacters = it.pageCharacters + 1) }
    }

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