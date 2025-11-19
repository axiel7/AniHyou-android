package com.axiel7.anihyou.feature.home.current

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.base.extensions.indexOfFirstOrNull
import com.axiel7.anihyou.core.common.utils.NumberUtils.isNullOrZero
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaListRepository
import com.axiel7.anihyou.core.model.CurrentListType
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.episodesBehind
import com.axiel7.anihyou.core.model.media.isBehind
import com.axiel7.anihyou.core.model.media.nextAnimeSeason
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaStatus
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CurrentViewModel(
    private val mediaListRepository: MediaListRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<CurrentUiState>(), CurrentEvent {

    override val initialState = CurrentUiState()

    private val myUserId = defaultPreferencesRepository.userId.filterNotNull()

    override fun refresh() {
        mutableUiState.update { it.copy(fetchFromNetwork = true) }
    }

    override fun onClickPlusOne(
        increment: Int,
        item: CommonMediaListEntry,
        type: CurrentListType
    ) {
        viewModelScope.launch {
            mutableUiState.update {
                it.copy(
                    selectedItem = item,
                    selectedType = type,
                    isLoadingPlusOne = true
                )
            }
            mediaListRepository.incrementProgress(
                entry = item.basicMediaListEntry,
                increment = increment,
                total = item.duration()
            ).collectLatest { result ->
                mutableUiState.update {
                    if (result is DataResult.Success && result.data != null) {
                        onUpdateListEntry(result.data!!.basicMediaListEntry, type)
                    }
                    result.toUiState().copy(isLoadingPlusOne = result is DataResult.Loading)
                }
            }
        }
    }

    override fun blockPlusOne() {
        mutableUiState.update { it.copy(isLoadingPlusOne = true) }
    }

    override fun onUpdateListEntry(
        newListEntry: BasicMediaListEntry?,
        type: CurrentListType
    ) {
        mutableUiState.value.run {
            selectedItem?.let { selectedItem ->
                if (selectedItem.basicMediaListEntry != newListEntry) {
                    val list = getListFromType(type)
                    if (newListEntry != null) {
                        list.indexOfFirstOrNull { it.mediaId == selectedItem.mediaId }
                            ?.let { index ->
                                val oldValue = list[index]
                                if (newListEntry.status != oldValue.basicMediaListEntry.status) {
                                    list.removeAt(index)
                                    if (newListEntry.status == MediaListStatus.COMPLETED
                                        && newListEntry.score.isNullOrZero()
                                    ) {
                                        toggleSetScoreDialog(true)
                                    }
                                } else {
                                    list[index] = oldValue.copy(basicMediaListEntry = newListEntry)
                                }
                                if (type == CurrentListType.BEHIND
                                    && !newListEntry.isBehind(oldValue.media?.nextAiringEpisode?.episode ?: 0)
                                ) {
                                    airingList.add(list[index])
                                    list.removeAt(index)
                                }
                            }
                    } else {
                        list.remove(selectedItem)
                    }
                }
            }
        }
    }

    override fun selectItem(item: CommonMediaListEntry, type: CurrentListType) {
        mutableUiState.update { it.copy(selectedItem = item, selectedType = type) }
    }

    override fun toggleSetScoreDialog(open: Boolean) {
        mutableUiState.update { it.copy(openSetScoreDialog = open) }
    }

    override fun setScore(score: Double?) {
        viewModelScope.launch {
            mutableUiState.value.selectedItem?.let { item ->
                mediaListRepository.updateEntry(
                    oldEntry = item.basicMediaListEntry,
                    mediaId = item.mediaId,
                    score = score,
                ).collectLatest {
                    if (it is DataResult.Success) toggleSetScoreDialog(false)
                }
            }
        }
    }

    init {
        defaultPreferencesRepository.scoreFormat
            .filterNotNull()
            .onEach { value ->
                mutableUiState.update { it.copy(scoreFormat = value) }
            }
            .launchIn(viewModelScope)

        // anime
        mutableUiState
            .distinctUntilChanged { _, new ->
                !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                mediaListRepository.getUserMediaList(
                    userId = myUserId.first(),
                    mediaType = MediaType.ANIME,
                    statusIn = listOf(MediaListStatus.CURRENT, MediaListStatus.REPEATING),
                    sort = listOf(MediaListSort.UPDATED_TIME_DESC),
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    page = null,
                    perPage = null,
                )
            }
            .onEach { result ->
                mutableUiState.update { uiState ->
                    when (result) {
                        is PagedResult.Success -> {
                            val airingList = result.list
                                .filter {
                                    it.media?.status == MediaStatus.RELEASING && !it.isBehind()
                                }
                                .sortedWith(
                                    compareBy(nullsLast()) { it.media?.nextAiringEpisode?.timeUntilAiring }
                                )
                            val behindList = result.list
                                .filter {
                                    it.media?.status == MediaStatus.RELEASING && it.isBehind()
                                }
                                .sortedBy { it.episodesBehind() }
                            val animeList = result.list
                                .filter { it.media?.status != MediaStatus.RELEASING }
                            uiState.airingList.clear()
                            uiState.airingList.addAll(airingList)
                            uiState.behindList.clear()
                            uiState.behindList.addAll(behindList)
                            uiState.animeList.clear()
                            uiState.animeList.addAll(animeList)
                            uiState.copy(
                                isLoading = false
                            )
                        }

                        is PagedResult.Loading -> {
                            uiState.copy(isLoading = true)
                        }

                        is PagedResult.Error -> {
                            uiState.copy(
                                error = result.message,
                                isLoading = false,
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)

        // manga
        mutableUiState
            .distinctUntilChanged { _, new ->
                !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                mediaListRepository.getUserMediaList(
                    userId = myUserId.first(),
                    mediaType = MediaType.MANGA,
                    statusIn = listOf(MediaListStatus.CURRENT, MediaListStatus.REPEATING),
                    sort = listOf(MediaListSort.UPDATED_TIME_DESC),
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    page = 1
                )
            }
            .onEach { result ->
                mutableUiState.update { uiState ->
                    when (result) {
                        is PagedResult.Success -> {
                            uiState.mangaList.clear()
                            uiState.mangaList.addAll(result.list)
                            uiState.copy(
                                fetchFromNetwork = false,
                                isLoading = false
                            )
                        }

                        is PagedResult.Loading -> {
                            uiState.copy(isLoading = true)
                        }

                        is PagedResult.Error -> {
                            uiState.copy(
                                error = result.message,
                                isLoading = false,
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)

        // next season on list
        mutableUiState
            .distinctUntilChanged { _, new ->
                !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                mediaListRepository.getMySeasonalAnime(
                    animeSeason = LocalDateTime.now().nextAnimeSeason(),
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    page = 1,
                )
            }
            .onEach { result ->
                mutableUiState.update { uiState ->
                    when (result) {
                        is PagedResult.Success -> {
                            uiState.nextSeasonAnimeList.clear()
                            uiState.nextSeasonAnimeList.addAll(result.list)
                            uiState.copy(
                                fetchFromNetwork = false,
                                isLoading = false
                            )
                        }

                        is PagedResult.Loading -> {
                            uiState.copy(isLoading = true)
                        }

                        is PagedResult.Error -> {
                            uiState.copy(
                                error = result.message,
                                isLoading = false,
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}