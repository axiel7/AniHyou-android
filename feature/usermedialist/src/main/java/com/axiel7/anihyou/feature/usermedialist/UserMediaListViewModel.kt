package com.axiel7.anihyou.feature.usermedialist

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.base.extensions.firstBlocking
import com.axiel7.anihyou.core.base.extensions.indexOfFirstOrNull
import com.axiel7.anihyou.core.common.utils.NumberUtils.isNullOrZero
import com.axiel7.anihyou.core.common.utils.StringUtils.fuzzyScore
import com.axiel7.anihyou.core.common.utils.StringUtils.whiteSpaceRegex
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.ListPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaListRepository
import com.axiel7.anihyou.core.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.model.media.ListType
import com.axiel7.anihyou.core.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.core.model.media.asMediaListStatus
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.isDescending
import com.axiel7.anihyou.core.model.media.isTitle
import com.axiel7.anihyou.core.model.media.titleComparator
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaStatus
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.InjectedParam
import kotlin.collections.emptyList

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class UserMediaListViewModel(
    @InjectedParam arguments: Route.UserMediaList,
    private val mediaListRepository: MediaListRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    private val listPreferencesRepository: ListPreferencesRepository,
) : UiStateViewModel<UserMediaListUiState>(), UserMediaListEvent {

    private val scoreFormat = arguments.scoreFormat?.let { ScoreFormat.safeValueOf(it) }
    private val mediaType = MediaType.safeValueOf(arguments.mediaType)
    private val lastSelectedList =
        (if (mediaType == MediaType.ANIME) listPreferencesRepository.animeListSelected
        else listPreferencesRepository.mangaListSelected).firstBlocking()

    override val initialState =
        UserMediaListUiState(
            mediaType = mediaType,
            scoreFormat = scoreFormat ?: ScoreFormat.POINT_10,
            selectedListName = lastSelectedList,
            status = lastSelectedList?.asMediaListStatus(),
            userId = arguments.userId.takeIf { it != 0 },
            isMyList = arguments.userId == 0
        )

    private val myUserId = defaultPreferencesRepository.userId
        .filterNotNull()

    private val titleLanguage = defaultPreferencesRepository.titleLanguage

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
                isLoading = true
            )
        }
    }

    override fun onClickPlusOne(increment: Int, entry: CommonMediaListEntry) {
        viewModelScope.launch {
            mutableUiState.update { it.copy(selectedItem = entry, isLoadingPlusOne = true) }
            mediaListRepository.incrementProgress(
                entry = entry.basicMediaListEntry,
                increment = increment,
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

    override fun blockPlusOne() {
        mutableUiState.update { it.copy(isLoadingPlusOne = true) }
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
            mutableUiState.value.run {
                if (isLoading) return@launch
                selectedItem?.let { item ->
                    mediaListRepository.updateEntry(
                        oldEntry = item.basicMediaListEntry,
                        mediaId = item.mediaId,
                        score = score,
                    ).collectLatest { result ->
                        mutableUiState.update {
                            result.toUiState()
                        }
                    }
                }
            }
            toggleScoreDialog(false)
        }
    }

    override fun toggleScoreDialog(open: Boolean) {
        mutableUiState.update { it.copy(openSetScoreDialog = open) }
    }

    override fun getRandomEntry() {
        uiState.value.run {
            mutableUiState.update { it.copy(isLoadingRandom = true) }
            val filteredList = entries.filter { entry ->
                formatMatch(entry)
                        && statusMatch(entry)
                        && countryMatch(entry)
                        && yearMatch(entry)
                        && genreMatch(entry)
                        && tagMatch(entry)
            }
            if (filteredList.isNotEmpty()) {
                mutableUiState.update {
                    it.copy(
                        randomEntryId = filteredList.random().mediaId,
                        isLoadingRandom = false,
                    )
                }
            } else {
                mutableUiState.update {
                    it.copy(errorId = R.string.no_media, isLoadingRandom = false)
                }
            }
        }
    }

    override fun onRandomEntryOpened() {
        mutableUiState.update { it.copy(randomEntryId = null) }
    }

    override fun setQuery(query: String) {
        mutableUiState.update { it.copy(query = query) }
    }

    override fun setMediaFormat(value: MediaFormatLocalizable?) {
        mutableUiState.update { it.copy(mediaFormat = value) }
    }

    override fun setMediaStatus(value: MediaStatus?) {
        mutableUiState.update { it.copy(mediaStatus = value) }
    }

    override fun setCountry(value: CountryOfOrigin?) {
        mutableUiState.update { it.copy(country = value) }
    }

    override fun setYear(value: Int?) {
        mutableUiState.update { it.copy(year = value) }
    }

    override fun onGenreTagStateChanged(value: GenresAndTagsForSearch) {
        mutableUiState.update {
            it.copy(genresAndTagsForSearch = value, clearedFilters = false)
        }
    }

    override fun clearFilters() {
        mutableUiState.update {
            it.copy(
                mediaFormat = null,
                mediaStatus = null,
                country = null,
                year = null,
                genresAndTagsForSearch = GenresAndTagsForSearch(),
                clearedFilters = true,
            )
        }
    }

    override fun onErrorDisplayed() {
        mutableUiState.update { it.copy(error = null, errorId = null) }
    }

    private fun UserMediaListUiState.formatMatch(entry: CommonMediaListEntry) =
        mediaFormat?.value?.let { entry.media?.format == it } ?: true

    private fun UserMediaListUiState.statusMatch(entry: CommonMediaListEntry) =
        mediaStatus?.let { entry.media?.status == it } ?: true

    private fun UserMediaListUiState.countryMatch(entry: CommonMediaListEntry) =
        country?.toDto()?.let { entry.media?.countryOfOrigin == it } ?: true

    private fun UserMediaListUiState.yearMatch(entry: CommonMediaListEntry) =
        year?.let { entry.media?.seasonYear == it } ?: true

    private fun UserMediaListUiState.genreMatch(entry: CommonMediaListEntry) =
        genresAndTagsForSearch.genreMatch(entry)

    private fun UserMediaListUiState.tagMatch(entry: CommonMediaListEntry) =
        genresAndTagsForSearch.tagMatch(entry)

    private fun GenresAndTagsForSearch.genreMatch(entry: CommonMediaListEntry): Boolean {
        val genres = entry.media?.genres ?: return true
        val genreInMatch = if (genreIn.isNotEmpty()) {
            genres.any { genreIn.contains(it) }
        } else true

        val genreNotMatch = if (genreNot.isNotEmpty()) {
            !genres.any { genreNot.contains(it) }
        } else true

        return genreInMatch && genreNotMatch
    }

    private fun GenresAndTagsForSearch.tagMatch(entry: CommonMediaListEntry): Boolean {
        val tags = entry.media?.tags
            ?.filter { (it?.rank ?: 0) >= minimumTagPercentage } ?: return true

        val tagInMatch = if (tagIn.isNotEmpty()) {
            tags.any { tagIn.contains(it?.name) }
        } else true

        val tagNotMatch = if (tagNot.isNotEmpty()) {
            !tags.any { tagNot.contains(it?.name) }
        } else true

        return tagInMatch && tagNotMatch
    }

    init {
        //search
        mutableUiState
            .distinctUntilChanged { old, new ->
                old.query == new.query
                        && old.mediaFormat == new.mediaFormat
                        && old.mediaStatus == new.mediaStatus
                        && old.country == new.country
                        && old.year == new.year
                        && old.genresAndTagsForSearch == new.genresAndTagsForSearch
                        && old.clearedFilters == new.clearedFilters
            }


            .debounce { uiState ->
                val totalItems = if (uiState.selectedListName != null) {
                    uiState.lists[uiState.selectedListName].orEmpty().size
                } else {
                    uiState.lists.values.sumOf { it.size }
                }

                when {
                    totalItems <= 20 -> 0L
                    totalItems >= 1000 -> 300L
                    else -> (totalItems * 0.3).toLong()
                }
            }
            .onEach { uiState ->
                val startTime = System.currentTimeMillis()

                val filteredList = withContext(Dispatchers.Default) {

                    val queryText = uiState.query.trim().lowercase()
                    val isQueryNotBlank = queryText.isNotBlank()

                    val queryTokens = if (isQueryNotBlank) {
                        queryText.split(whiteSpaceRegex).filter { it.isNotEmpty() }
                    } else emptyList()

                    val baseEntries = if (uiState.selectedListName != null) {
                        uiState.lists[uiState.selectedListName].orEmpty()
                    } else {
                        uiState.lists.values.flatten().distinctBy { it.mediaId }
                    }

                    if (uiState.filterCount > 0 || isQueryNotBlank) {

                        val scoredEntries = baseEntries.mapNotNull { entry ->

                            val matchesFilters = uiState.formatMatch(entry)
                                    && uiState.statusMatch(entry)
                                    && uiState.countryMatch(entry)
                                    && uiState.yearMatch(entry)
                                    && uiState.genreMatch(entry)
                                    && uiState.tagMatch(entry)

                            if (!matchesFilters) return@mapNotNull null

                            if (isQueryNotBlank) {
                                val title = entry.media?.title
                                val romajiScore =
                                    title?.romaji.fuzzyScore(queryText, queryTokens) ?: 0
                                val englishScore =
                                    title?.english.fuzzyScore(queryText, queryTokens) ?: 0
                                val nativeScore =
                                    title?.native.fuzzyScore(queryText, queryTokens) ?: 0

                                val synonymScore = entry.media?.synonyms?.maxOfOrNull { syn ->
                                    syn.fuzzyScore(queryText, queryTokens)
                                } ?: 0

                                val maxScore =
                                    maxOf(romajiScore, englishScore, nativeScore, synonymScore)

                                if (maxScore > 0) {
                                    Pair(entry, maxScore)
                                } else {
                                    null
                                }
                            } else {
                                Pair(entry, 0)
                            }
                        }

                        if (isQueryNotBlank) {
                            scoredEntries.sortedByDescending { it.second }.map { it.first }
                        } else {
                            scoredEntries.map { it.first }
                        }
                    } else {
                        baseEntries
                    }
                }
                val executionTimeMs = System.currentTimeMillis() - startTime
                android.util.Log.d(
                    "SearchPerf",
                    "Query: '${uiState.query}' | Results: ${filteredList.size} | Time: ${executionTimeMs}ms"
                )

                mutableUiState.update { state ->
                    state.entries.apply {
                        clear()
                        addAll(filteredList)
                    }
                    state
                }
            }
            .launchIn(viewModelScope)

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

        // get value from settings
        defaultPreferencesRepository.showLowPriority
            .distinctUntilChanged()
            .onEach { value ->
                mutableUiState.update { it.copy(showLowPriority = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.colorLowPriority
            .onEach { color ->
                mutableUiState.update { it.copy(lowPriorityColor = Color(color)) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.colorMediumPriority
            .onEach { color ->
                mutableUiState.update { it.copy(mediumPriorityColor = Color(color)) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.colorHighPriority
            .onEach { color ->
                mutableUiState.update { it.copy(highPriorityColor = Color(color)) }
            }
            .launchIn(viewModelScope)

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
            .distinctUntilChanged()
            .onEach { sort ->
                mutableUiState.update {
                    it.copy(sort = sort, isLoading = true)
                }
            }
            .launchIn(viewModelScope)

        // section order and custom lists
        when (mediaType) {
            MediaType.ANIME -> defaultPreferencesRepository.animeLists
            MediaType.MANGA -> defaultPreferencesRepository.mangaLists
            else -> emptyFlow()
        }
            .distinctUntilChanged()
            .onEach { listNames ->
                mutableUiState.update { it.copy(orderedListNames = listNames) }
            }
            .launchIn(viewModelScope)

        mutableUiState
            .distinctUntilChanged { old, new ->
                old.sort == new.sort
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
                        if (result.currentPage == 1 || result.currentPage == null) {
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
                            fetchFromNetwork = false,
                            isLoading = loadMore,
                        )
                    } else {
                        if (result is PagedResult.Error) {
                            uiState.setError(result.message)
                        }
                        uiState.setLoading(result is PagedResult.Loading)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}