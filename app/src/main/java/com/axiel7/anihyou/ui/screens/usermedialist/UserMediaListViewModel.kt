package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.common.firstBlocking
import com.axiel7.anihyou.common.indexOfFirstOrNull
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.media.ListType
import com.axiel7.anihyou.data.model.media.asMediaListStatus
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.isDescending
import com.axiel7.anihyou.data.model.media.isTitle
import com.axiel7.anihyou.data.model.media.titleComparator
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
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.utils.DateUtils.toFuzzyDate
import com.axiel7.anihyou.utils.NumberUtils.isGreaterThanZero
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlinx.coroutines.withContext
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel(assistedFactory = UserMediaListViewModel.Factory::class)
class UserMediaListViewModel @AssistedInject constructor(
    @Assisted private val mediaType: MediaType,
    savedStateHandle: SavedStateHandle,
    private val mediaListRepository: MediaListRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    private val listPreferencesRepository: ListPreferencesRepository,
) : PagedUiStateViewModel<UserMediaListUiState>(), UserMediaListEvent {

    @AssistedFactory
    interface Factory {
        fun create(mediaType: MediaType): UserMediaListViewModel
    }

    private val arguments = runCatching { savedStateHandle.toRoute<UserMediaList>() }.getOrNull()
    private val scoreFormat = arguments?.scoreFormat?.let { ScoreFormat.safeValueOf(it) }

    private val lastSelectedList =
        (if (mediaType == MediaType.ANIME) listPreferencesRepository.animeListSelected
        else listPreferencesRepository.mangaListSelected).firstBlocking()

    override val initialState =
        UserMediaListUiState(
            mediaType = mediaType,
            scoreFormat = scoreFormat ?: ScoreFormat.POINT_10,
            selectedListName = lastSelectedList,
            status = lastSelectedList?.asMediaListStatus(),
            userId = arguments?.userId.takeIf { it != 0 },
            isMyList = arguments == null || arguments.userId == 0
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
        viewModelScope.launch {
            mutableUiState.update {
                it.entries.clear()
                it.entries.addAll(it.getEntriesFromListName(listName))
                it.copy(
                    selectedListName = listName,
                    status = listName?.asMediaListStatus()
                )
            }
            if (mediaType == MediaType.ANIME) {
                listPreferencesRepository.setAnimeListSelected(listName)
            } else {
                listPreferencesRepository.setMangaListSelected(listName)
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

    private fun updateEntryProgress(
        mediaId: Int,
        progress: Int,
        status: MediaListStatus? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
    ) {
        if (mutableUiState.value.isLoading) return
        mediaListRepository.updateEntry(
            mediaId = mediaId,
            progress = progress,
            status = status,
            startedAt = startDate?.toFuzzyDate(),
            completedAt = endDate?.toFuzzyDate(),
        ).onEach { result ->
            mutableUiState.update { uiState ->
                if (result is DataResult.Success
                    && result.data != null
                    && uiState.selectedListName != null
                ) {
                    uiState.entries.indexOfFirstOrNull { it.mediaId == mediaId }?.let { index ->
                        val oldValue = uiState.entries[index]
                        if (result.data.basicMediaListEntry.status != oldValue.basicMediaListEntry.status) {
                            uiState.entries.removeAt(index)
                        } else {
                            uiState.entries[index] = oldValue.copy(
                                basicMediaListEntry = result.data.basicMediaListEntry
                            )
                        }
                        uiState.lists[uiState.selectedListName] = uiState.entries
                    }
                }
                result.toUiState()
            }
        }.launchIn(viewModelScope)
    }

    override fun onClickPlusOne(entry: CommonMediaListEntry) {
        val newProgress = (entry.basicMediaListEntry.progress ?: 0) + 1
        val totalDuration = entry.media?.basicMediaDetails?.duration()
        val isMaxProgress = totalDuration != null && newProgress >= totalDuration
        val isPlanning = entry.basicMediaListEntry.status == MediaListStatus.PLANNING
        val newStatus = when {
            isMaxProgress -> MediaListStatus.COMPLETED
            isPlanning -> MediaListStatus.CURRENT
            else -> null
        }
        updateEntryProgress(
            mediaId = entry.mediaId,
            progress = newProgress,
            status = newStatus,
            startDate = LocalDate.now().takeIf {
                isPlanning || !entry.basicMediaListEntry.progress.isGreaterThanZero()
            },
            endDate = LocalDate.now().takeIf { isMaxProgress },
        )
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
                val sort = if (uiState.sort.isTitle()) {
                    listOf(MediaListSort.MEDIA_ID)
                } else {
                    listOf(uiState.sort)
                }
                val loadByChunk = uiState.sort == MediaListSort.UPDATED_TIME_DESC
                val chunk = uiState.page.takeIf { loadByChunk }
                val perChunk = (100).takeIf { loadByChunk }
                mediaListRepository.getMediaListCollection(
                    userId = listUserId,
                    mediaType = mediaType,
                    sort = sort,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    chunk = chunk,
                    perChunk = perChunk
                )
            }
            .onEach { result ->
                mutableUiState.update { uiState ->
                    if (result is PagedResult.Success) {
                        if (uiState.page == 1 || result.currentPage == 1) {
                            uiState.lists.clear()
                            uiState.entries.clear()
                        }
                        val newEntries = mutableListOf<CommonMediaListEntry>()
                        result.list.forEach { list ->
                            list?.name?.let { name ->
                                var entries = list.entries?.mapNotNull { it?.commonMediaListEntry }
                                    .orEmpty()
                                if (uiState.sort.isTitle()) {
                                    withContext(Dispatchers.IO) {
                                        entries = entries.sortedWith(
                                            titleComparator(desc = uiState.sort.isDescending())
                                        )
                                    }
                                }
                                uiState.lists[name] = uiState.lists[name].orEmpty() + entries
                                if (uiState.selectedListName == null && list.isCustomList == false) {
                                    newEntries.addAll(entries)
                                } else if (name == uiState.selectedListName) {
                                    newEntries.addAll(entries)
                                }
                            }
                        }
                        uiState.entries.addAll(newEntries)
                        uiState.copy(
                            page = if (newEntries.isEmpty() && result.hasNextPage) uiState.page + 1
                            else uiState.page,
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