package com.axiel7.anihyou.feature.usermedialist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.core.common.DataResult
import com.axiel7.anihyou.core.common.PagedResult
import com.axiel7.anihyou.core.common.extensions.firstBlocking
import com.axiel7.anihyou.core.common.extensions.indexOfFirstOrNull
import com.axiel7.anihyou.core.common.utils.NumberUtils.isNullOrZero
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.ListPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaListRepository
import com.axiel7.anihyou.core.model.media.ListType
import com.axiel7.anihyou.core.model.media.asMediaListStatus
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.isDescending
import com.axiel7.anihyou.core.model.media.isTitle
import com.axiel7.anihyou.core.model.media.titleComparator
import com.axiel7.anihyou.core.network.api.model.toFuzzyDate
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.common.viewmodel.PagedUiStateViewModel
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
class UserMediaListViewModel(
    private val mediaType: MediaType,
    savedStateHandle: SavedStateHandle,
    private val mediaListRepository: MediaListRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    private val listPreferencesRepository: ListPreferencesRepository,
) : PagedUiStateViewModel<UserMediaListUiState>(), UserMediaListEvent {

    private val arguments =
        runCatching { savedStateHandle.toRoute<Routes.UserMediaList>() }.getOrNull()
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
                if (listName != null) {
                    it.entries.addAll(it.lists[listName].orEmpty())
                } else {
                    it.entries.addAll(it.lists.values.flatten())
                }
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

    private suspend fun updateEntry(
        mediaId: Int,
        progress: Int? = null,
        status: MediaListStatus? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        score: Double? = null,
        isFromScoreDialog: Boolean = false,
    ) {
        if (mutableUiState.value.isLoading) return
        mediaListRepository.updateEntry(
            mediaId = mediaId,
            progress = progress,
            status = status,
            score = score,
            startedAt = startDate?.toFuzzyDate(),
            completedAt = endDate?.toFuzzyDate(),
        ).collectLatest { result ->
            mutableUiState.update {
                if (result is DataResult.Success && result.data != null && !isFromScoreDialog) {
                    onUpdateListEntry(result.data!!.basicMediaListEntry)
                }
                result.toUiState()
            }
        }
    }

    override fun onClickPlusOne(entry: CommonMediaListEntry) {
        viewModelScope.launch {
            mutableUiState.update { it.copy(selectedItem = entry, isLoadingPlusOne = true) }
            mediaListRepository.incrementOneProgress(
                entry = entry.basicMediaListEntry,
                total = entry.duration()
            ).collectLatest { result ->
                mutableUiState.update {
                    if (result is DataResult.Success && result.data != null) {
                        onUpdateListEntry(result.data!!.basicMediaListEntry)
                    }
                    result.toUiState().copy(isLoadingPlusOne = result is DataResult.Loading)
                }
            }
        }
    }

    override fun selectItem(value: CommonMediaListEntry?) {
        mutableUiState.update { it.copy(selectedItem = value) }
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        mutableUiState.value.run {
            selectedItem?.let { selectedItem ->
                if (selectedItem.basicMediaListEntry != newListEntry) {
                    if (newListEntry != null) {
                        entries.indexOfFirstOrNull { it.mediaId == selectedItem.mediaId }
                            ?.let { index ->
                                val oldValue = entries[index]
                                if (newListEntry.status != oldValue.basicMediaListEntry.status) {
                                    entries.removeAt(index)
                                    if (newListEntry.status == MediaListStatus.COMPLETED
                                        && newListEntry.score.isNullOrZero()
                                    ) {
                                        mutableUiState.update { it.copy(openSetScoreDialog = true) }
                                    }
                                } else {
                                    entries[index] =
                                        oldValue.copy(basicMediaListEntry = newListEntry)
                                }
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

    override fun setScore(score: Double?) {
        viewModelScope.launch {
            mutableUiState.value.selectedItem?.mediaId?.let { mediaId ->
                updateEntry(
                    mediaId = mediaId,
                    score = score,
                    isFromScoreDialog = true,
                )
            }
            toggleScoreDialog(false)
        }
    }

    override fun toggleScoreDialog(open: Boolean) {
        mutableUiState.update { it.copy(openSetScoreDialog = open) }
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
                                randomEntryId = if (!result.hasNextPage && newList.isNotEmpty())
                                    newList.random() else null,
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
        mutableUiState
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
                mutableUiState
                    .distinctUntilChangedBy { it.status }
                    .collectLatest { uiState ->
                        listPreferencesRepository.stylePreference(
                            listType = ListType(
                                status = uiState.status ?: MediaListStatus.CURRENT,
                                mediaType = mediaType,
                            )
                        )?.collectLatest { style ->
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
                    it.copy(sort = sort, page = 1, hasNextPage = true, isLoading = true)
                }
            }
            .launchIn(viewModelScope)

        // section order and custom lists
        when (mediaType) {
            MediaType.ANIME -> defaultPreferencesRepository.animeLists
            MediaType.MANGA -> defaultPreferencesRepository.mangaLists
            else -> emptyFlow()
        }
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { listNames ->
                mutableUiState.update { it.copy(orderedListNames = listNames) }
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
                mediaListRepository.getMediaListCollection(
                    userId = listUserId,
                    mediaType = mediaType,
                    sort = sort,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    chunk = null,
                    perChunk = null
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
                        val loadMore = newEntries.isEmpty() && result.hasNextPage
                        uiState.copy(
                            page = if (loadMore) uiState.page + 1 else uiState.page,
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
                            isLoading = loadMore,
                        )
                    } else {
                        result.toUiState(
                            loadingWhen = uiState.page == 1
                                    || (uiState.entries.isEmpty() && uiState.hasNextPage)
                        ).copy(
                            hasNextPage = if (result is PagedResult.Error) false
                            else uiState.hasNextPage
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}