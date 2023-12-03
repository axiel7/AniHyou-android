package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.media.ListType
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.ListPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaListRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.NavArgument
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserMediaListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaListRepository: MediaListRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    private val listPreferencesRepository: ListPreferencesRepository,
) : PagedUiStateViewModel<UserMediaListUiState>() {

    val mediaType =
        savedStateHandle.getStateFlow(NavArgument.MediaType.name, MediaType.UNKNOWN__.name)
            .map { MediaType.safeValueOf(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, MediaType.UNKNOWN__)

    val userId: Int? = savedStateHandle[NavArgument.UserId.name]
    val isMyList = userId == null

    private val scoreFormatArg: String? = savedStateHandle[NavArgument.ScoreFormat.name]

    private val myUserId = defaultPreferencesRepository.userId
        .filterNotNull()

    override val mutableUiState = MutableStateFlow(
        UserMediaListUiState(
            scoreFormat = scoreFormatArg?.let { ScoreFormat.valueOf(it) } ?: ScoreFormat.POINT_10
        )
    )
    override val uiState = mutableUiState.asStateFlow()

    fun setScoreFormat(value: ScoreFormat) = mutableUiState.update { it.copy(scoreFormat = value) }

    fun setStatus(value: MediaListStatus) = mutableUiState.update {
        it.copy(status = value, page = 1, hasNextPage = true)
    }

    fun setSort(value: MediaListSort) = viewModelScope.launch {
        if (mediaType.value == MediaType.ANIME)
            defaultPreferencesRepository.setAnimeListSort(value)
        else if (mediaType.value == MediaType.MANGA)
            defaultPreferencesRepository.setMangaListSort(value)
    }

    fun toggleSortMenu(open: Boolean) = mutableUiState.update { it.copy(sortMenuExpanded = open) }

    fun toggleNotesDialog(open: Boolean) = mutableUiState.update { it.copy(openNotesDialog = open) }

    fun refreshList() = mutableUiState.update {
        it.copy(
            fetchFromNetwork = true,
            page = 1,
            hasNextPage = true,
            isLoading = true
        )
    }

    fun updateEntryProgress(
        entryId: Int,
        progress: Int
    ) {
        mediaListRepository.updateEntryProgress(
            entryId = entryId,
            progress = progress,
        ).onEach { result ->
            mutableUiState.update {
                if (result is DataResult.Success && result.data != null) {
                    val foundIndex = media.indexOfFirst { it.basicMediaListEntry.id == entryId }
                    if (foundIndex != -1) {
                        if (result.data.status != media[foundIndex].basicMediaListEntry.status) {
                            media.removeAt(foundIndex)
                        } else {
                            media[foundIndex] = media[foundIndex].copy(
                                basicMediaListEntry = media[foundIndex].basicMediaListEntry.copy(
                                    progress = progress
                                )
                            )
                        }
                    }
                }
                result.toUiState()
            }
        }.launchIn(viewModelScope)
    }

    var selectedItem: UserMediaListQuery.MediaList? = null
        private set

    fun selectItem(value: UserMediaListQuery.MediaList?) {
        selectedItem = value
    }

    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        if (selectedItem != null && selectedItem?.basicMediaListEntry != newListEntry) {
            if (newListEntry != null) {
                val index = media.indexOf(selectedItem)
                if (index != -1) {
                    media[index] = selectedItem!!.copy(basicMediaListEntry = newListEntry)
                }
            } else {
                media.remove(selectedItem)
            }
        }
    }

    val itemsPerRow = listPreferencesRepository.gridItemsPerRow.stateInViewModel()

    val media = mutableStateListOf<UserMediaListQuery.MediaList>()

    init {
        if (isMyList) {
            // score format
            defaultPreferencesRepository.scoreFormat
                .filterNotNull()
                .onEach { format ->
                    mutableUiState.update { it.copy(scoreFormat = format) }
                }
                .launchIn(viewModelScope)
        }

        // list style
        combine(
            mediaType,
            listPreferencesRepository.useGeneralListStyle,
            listPreferencesRepository.generalListStyle
        ) { mediaType, useGeneral, generalStyle ->
            if (useGeneral) {
                mutableUiState.update { it.copy(listStyle = generalStyle) }
            } else {
                uiState
                    .distinctUntilChangedBy { it.status }
                    .collectLatest { uiState ->
                        ListType(uiState.status, mediaType)
                            .stylePreference(listPreferencesRepository)
                            ?.collectLatest { style ->
                                mutableUiState.update { it.copy(listStyle = style) }
                            }
                    }
            }
        }.launchIn(viewModelScope)

        // sort preference
        mediaType
            .flatMapLatest {
                when (it) {
                    MediaType.ANIME -> defaultPreferencesRepository.animeListSort
                    MediaType.MANGA -> defaultPreferencesRepository.mangaListSort
                    else -> emptyFlow()
                }
            }
            .filterNotNull()
            .onEach { sort ->
                mutableUiState.update {
                    it.copy(sort = sort, page = 1, hasNextPage = true)
                }
            }
            .launchIn(viewModelScope)

        mutableUiState
            .filter { it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.status == new.status
                        && old.sort == new.sort
                        && !new.fetchFromNetwork
            }
            .combine(mediaType, ::Pair)
            .flatMapLatest { (uiState, mediaType) ->
                val listUserId = userId ?: myUserId.first()
                mediaListRepository.getUserMediaListPage(
                    userId = listUserId,
                    mediaType = mediaType,
                    status = uiState.status,
                    sort = uiState.sort,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    page = uiState.page
                )
            }
            .onEach { result ->
                if (result is PagedResult.Success) {
                    mutableUiState.update {
                        if (it.page == 1) media.clear()
                        media.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
                            isLoading = false,
                        )
                    }
                } else {
                    mutableUiState.update {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}