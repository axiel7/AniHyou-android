package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.media.ListType
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.ListPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaListRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.type.UserTitleLanguage
import com.axiel7.anihyou.ui.common.navigation.NavArgument
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
) : PagedUiStateViewModel<UserMediaListUiState>(), UserMediaListEvent {

    private val mediaType =
        savedStateHandle.getStateFlow(NavArgument.MediaType.name, MediaType.UNKNOWN__.name)
            .map { MediaType.safeValueOf(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, MediaType.UNKNOWN__)

    private val userId: StateFlow<Int?> =
        savedStateHandle.getStateFlow(NavArgument.UserId.name, null)

    private val scoreFormatArg: String? = savedStateHandle[NavArgument.ScoreFormat.name]

    override val initialState =
        UserMediaListUiState(
            mediaType = mediaType.value,
            scoreFormat = scoreFormatArg?.let { ScoreFormat.valueOf(it) } ?: ScoreFormat.POINT_10
        )

    private val myUserId = defaultPreferencesRepository.userId
        .filterNotNull()

    private val titleLanguage = defaultPreferencesRepository.titleLanguage

    fun setIsCompactScreen(value: Boolean) {
        mutableUiState.update { it.copy(isCompactScreen = value) }
    }

    override fun setScoreFormat(value: ScoreFormat) {
        mutableUiState.update { it.copy(scoreFormat = value) }
    }

    override fun setStatus(value: MediaListStatus?) {
        viewModelScope.launch {
            if (mediaType.value == MediaType.ANIME) {
                listPreferencesRepository.setAnimeListStatus(value)
            } else if (mediaType.value == MediaType.MANGA) {
                listPreferencesRepository.setMangaListStatus(value)
            }
        }
    }

    override fun setSort(value: MediaListSort) {
        viewModelScope.launch {
            var sort = value
            // when sorting by title, change according to the user preferred title lang
            if (sort == MediaListSort.MEDIA_TITLE_ROMAJI
                || sort == MediaListSort.MEDIA_TITLE_ROMAJI_DESC
            ) {
                val isDesc = sort == MediaListSort.MEDIA_TITLE_ROMAJI_DESC
                sort = when (titleLanguage.first()) {
                    UserTitleLanguage.ENGLISH,
                    UserTitleLanguage.ENGLISH_STYLISED ->
                        if (isDesc) MediaListSort.MEDIA_TITLE_ENGLISH_DESC
                        else MediaListSort.MEDIA_TITLE_ENGLISH

                    UserTitleLanguage.NATIVE,
                    UserTitleLanguage.NATIVE_STYLISED ->
                        if (isDesc) MediaListSort.MEDIA_TITLE_NATIVE_DESC
                        else MediaListSort.MEDIA_TITLE_NATIVE

                    else -> value
                }
            }
            if (mediaType.value == MediaType.ANIME) {
                listPreferencesRepository.setAnimeListSort(sort)
            } else if (mediaType.value == MediaType.MANGA) {
                listPreferencesRepository.setMangaListSort(sort)
            }
        }
    }

    override fun toggleSortMenu(open: Boolean) {
        mutableUiState.update { it.copy(sortMenuExpanded = open) }
    }

    override fun toggleNotesDialog(open: Boolean) {
        mutableUiState.update { it.copy(openNotesDialog = open) }
    }

    override fun refreshList() {
        mutableUiState.update {
            it.copy(
                fetchFromNetwork = true,
                page = 1,
                hasNextPage = true,
                isLoading = true
            )
        }
    }

    override fun updateEntryProgress(entryId: Int, progress: Int) {
        mediaListRepository.updateEntryProgress(
            entryId = entryId,
            progress = progress,
        ).onEach { result ->
            mutableUiState.update { uiState ->
                if (result is DataResult.Success && result.data != null) {
                    val foundIndex = uiState.media.indexOfFirst { it.id == entryId }
                    if (foundIndex != -1) {
                        if (result.data.status != uiState.media[foundIndex].basicMediaListEntry.status) {
                            uiState.media.removeAt(foundIndex)
                        } else {
                            uiState.media[foundIndex] = uiState.media[foundIndex].copy(
                                basicMediaListEntry = uiState.media[foundIndex].basicMediaListEntry.copy(
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

    override fun selectItem(value: CommonMediaListEntry?) {
        mutableUiState.update { it.copy(selectedItem = value) }
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        mutableUiState.value.run {
            selectedItem?.let { selectedItem ->
                if (selectedItem.basicMediaListEntry != newListEntry) {
                    if (newListEntry != null
                        && newListEntry.status == selectedItem.basicMediaListEntry.status
                    ) {
                        val index = media.indexOf(selectedItem)
                        if (index != -1) {
                            media[index] = selectedItem.copy(basicMediaListEntry = newListEntry)
                        }
                    } else {
                        media.remove(selectedItem)
                    }
                }
            }
        }
    }

    init {
        userId
            .onEach { value ->
                mutableUiState.update {
                    it.copy(
                        userId = value,
                        isMyList = value == null
                    )
                }
            }
            .launchIn(viewModelScope)

        mediaType
            .onEach { value ->
                mutableUiState.update { it.copy(mediaType = value) }
            }
            .launchIn(viewModelScope)

        // score format
        uiState
            .distinctUntilChangedBy { it.isMyList }
            .flatMapLatest {
                if (it.isMyList) {
                    defaultPreferencesRepository.scoreFormat.filterNotNull()
                } else emptyFlow()
            }
            .onEach { format ->
                mutableUiState.update { it.copy(scoreFormat = format) }
            }
            .launchIn(viewModelScope)

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
                        ListType(uiState.status ?: MediaListStatus.CURRENT, mediaType)
                            .stylePreference(listPreferencesRepository)
                            ?.collectLatest { style ->
                                mutableUiState.update { it.copy(listStyle = style) }
                            }
                    }
            }
        }.launchIn(viewModelScope)

        // grid items per row
        listPreferencesRepository.gridItemsPerRow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { value ->
                mutableUiState.update { it.copy(itemsPerRow = value) }
            }
            .launchIn(viewModelScope)

        // sort preference
        mediaType
            .flatMapLatest {
                when (it) {
                    MediaType.ANIME -> listPreferencesRepository.animeListSort
                    MediaType.MANGA -> listPreferencesRepository.mangaListSort
                    else -> emptyFlow()
                }
            }
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { sort ->
                mutableUiState.update {
                    it.copy(sort = sort, page = 1, hasNextPage = true)
                }
            }
            .launchIn(viewModelScope)

        // status preference
        mediaType
            .flatMapLatest {
                when (it) {
                    MediaType.ANIME -> listPreferencesRepository.animeListStatus
                    MediaType.MANGA -> listPreferencesRepository.mangaListStatus
                    else -> emptyFlow()
                }
            }
            .distinctUntilChanged()
            .onEach { status ->
                mutableUiState.update {
                    it.copy(status = status, page = 1, hasNextPage = true, isLoading = true)
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
                val listUserId = uiState.userId ?: myUserId.first()
                val sort = if (uiState.status == null) {
                    listOf(MediaListSort.STATUS, uiState.sort)
                } else {
                    listOf(uiState.sort, MediaListSort.MEDIA_ID_DESC)
                }
                mediaListRepository.getUserMediaListPage(
                    userId = listUserId,
                    mediaType = mediaType,
                    status = uiState.status,
                    sort = sort,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    page = uiState.page
                )
            }
            .onEach { result ->
                if (result is PagedResult.Success) {
                    mutableUiState.update {
                        if (it.page == 1 || result.currentPage == 1) it.media.clear()
                        it.media.addAll(result.list)
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