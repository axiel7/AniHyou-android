package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.common.indexOfFirstOrNull
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.media.ListType
import com.axiel7.anihyou.data.model.media.asMediaListStatus
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.ListPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaListRepository
import com.axiel7.anihyou.data.repository.WidgetRepository
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
import kotlinx.coroutines.flow.onEach
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
    private val widgetRepository: WidgetRepository,
) : PagedUiStateViewModel<UserMediaListUiState>(), UserMediaListEvent {

    private val mediaType =
        savedStateHandle.get<String>(NavArgument.MediaType.name)
            ?.let { MediaType.safeValueOf(it) } ?: MediaType.UNKNOWN__

    private val userId: StateFlow<Int?> =
        savedStateHandle.getStateFlow(NavArgument.UserId.name, null)

    private val scoreFormatArg: String? = savedStateHandle[NavArgument.ScoreFormat.name]

    override val initialState =
        UserMediaListUiState(
            mediaType = mediaType,
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

    override fun onChangeList(listName: String?) {
        mutableUiState.update {
            it.entries.clear()
            it.entries.addAll(it.getEntriesFromListName(listName))
            it.copy(
                selectedListName = listName,
                status = listName?.asMediaListStatus()
            )
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
            if (mediaType == MediaType.ANIME) {
                listPreferencesRepository.setAnimeListSort(sort)
            } else if (mediaType == MediaType.MANGA) {
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
        if (mutableUiState.value.isLoading) return
        mediaListRepository.updateEntryProgress(
            entryId = entryId,
            progress = progress,
        ).onEach { result ->
            mutableUiState.update { uiState ->
                if (result is DataResult.Success
                    && result.data != null
                    && uiState.selectedListName != null
                ) {
                    uiState.entries.indexOfFirstOrNull { it.id == entryId }?.let { foundIndex ->
                        val oldValue = uiState.entries[foundIndex]
                        if (result.data.status != oldValue.basicMediaListEntry.status) {
                            uiState.entries.removeAt(foundIndex)
                        } else {
                            uiState.entries[foundIndex] = oldValue.copy(
                                basicMediaListEntry = oldValue.basicMediaListEntry.copy(
                                    progress = progress
                                )
                            )
                        }
                        uiState.lists[uiState.selectedListName] = uiState.entries
                    }
                    widgetRepository.updateMediaListWidget()
                }
                result.toUiState()
            }
        }.launchIn(viewModelScope)
    }

    override fun onClickPlusOne(entry: BasicMediaListEntry) {
        if (!mutableUiState.value.isLoading) {
            super.onClickPlusOne(entry)
        }
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
                        val index = entries.indexOf(selectedItem)
                        if (index != -1) {
                            entries[index] = selectedItem.copy(basicMediaListEntry = newListEntry)
                        }
                    } else {
                        entries.remove(selectedItem)
                    }
                    selectedListName?.let { selectedListName ->
                        lists[selectedListName] = entries
                    }
                }
            }
        }
    }

    override fun getRandomPlannedEntry(chunk: Int) {
        if (chunk == 1 && mutableUiState.value.plannedEntriesIds.isNotEmpty()) {
            mutableUiState.update {
                it.copy(randomEntryId = it.plannedEntriesIds.random())
            }
        } else {
            viewModelScope.launch {
                var hasNextPage = false
                mediaListRepository.getMediaListIds(
                    userId = mutableUiState.value.userId ?: myUserId.first(),
                    type = mutableUiState.value.mediaType,
                    status = MediaListStatus.PLANNING,
                    chunk = chunk
                ).collectLatest { result ->
                    mutableUiState.update {
                        if (result is PagedResult.Success) {
                            hasNextPage = result.hasNextPage
                            val newList = it.plannedEntriesIds + result.list
                            it.copy(
                                plannedEntriesIds = newList,
                                randomEntryId = if (!result.hasNextPage) newList.random() else null,
                                isLoading = result.hasNextPage
                            )
                        } else result.toUiState()
                    }
                }
                if (hasNextPage) {
                    getRandomPlannedEntry(chunk + 1)
                }
            }
        }
    }

    override fun onRandomEntryOpened() {
        mutableUiState.update { it.copy(randomEntryId = null) }
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
            listPreferencesRepository.useGeneralListStyle,
            listPreferencesRepository.generalListStyle
        ) { useGeneral, generalStyle ->
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
        when (mediaType) {
            MediaType.ANIME -> listPreferencesRepository.animeListSort
            MediaType.MANGA -> listPreferencesRepository.mangaListSort
            else -> emptyFlow()
        }
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { sort ->
                mutableUiState.update {
                    it.copy(sort = sort, page = 1, hasNextPage = true)
                }
            }
            .launchIn(viewModelScope)

        // custom lists
        when (mediaType) {
            MediaType.ANIME -> defaultPreferencesRepository.animeCustomLists
            MediaType.MANGA -> defaultPreferencesRepository.mangaCustomLists
            else -> emptyFlow()
        }
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { customLists ->
                mutableUiState.update { it.copy(customLists = customLists) }
            }
            .launchIn(viewModelScope)

        mutableUiState
            .filter { it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.sort == new.sort
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                val listUserId = uiState.userId ?: myUserId.first()
                mediaListRepository.getMediaListCollection(
                    userId = listUserId,
                    mediaType = mediaType,
                    sort = listOf(uiState.sort),
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    chunk = uiState.page,
                    perChunk = 50
                )
            }
            .onEach { result ->
                mutableUiState.update { uiState ->
                    if (result is PagedResult.Success) {
                        if (uiState.page == 1 || result.currentPage == 1) {
                            uiState.lists.clear()
                            uiState.entries.clear()
                        }
                        var newEntries = emptyList<CommonMediaListEntry>()
                        result.list.forEach { list ->
                            list?.name?.let { name ->
                                val entries = list.entries?.mapNotNull { it?.commonMediaListEntry }
                                    .orEmpty()
                                uiState.lists[name] = uiState.lists[name].orEmpty() + entries
                                if (name == uiState.selectedListName) {
                                    newEntries = entries
                                }
                            }
                        }
                        if (uiState.selectedListName == null) {
                            uiState.entries.addAll(uiState.lists.values.flatten())
                        } else {
                            uiState.entries.addAll(newEntries)
                        }
                        uiState.copy(
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
                            isLoading = false,
                        )
                    } else {
                        result.toUiState(loadingWhen = uiState.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}