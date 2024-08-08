package com.axiel7.anihyou.ui.screens.home.current

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.common.indexOfFirstOrNull
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.api.response.PagedResult
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.episodesBehind
import com.axiel7.anihyou.data.model.media.isBehind
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaListRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.ui.screens.home.current.CurrentUiState.Companion.ListType.AIRING
import com.axiel7.anihyou.ui.screens.home.current.CurrentUiState.Companion.ListType.ANIME
import com.axiel7.anihyou.ui.screens.home.current.CurrentUiState.Companion.ListType.BEHIND
import com.axiel7.anihyou.ui.screens.home.current.CurrentUiState.Companion.ListType.MANGA
import com.axiel7.anihyou.utils.NumberUtils.isNullOrZero
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CurrentViewModel @Inject constructor(
    private val mediaListRepository: MediaListRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<CurrentUiState>(), CurrentEvent {

    override val initialState = CurrentUiState()

    private val myUserId = defaultPreferencesRepository.userId.filterNotNull()

    override fun refresh() {
        mutableUiState.update { it.copy(fetchFromNetwork = true) }
    }

    override fun onClickPlusOne(
        item: CommonMediaListEntry,
        type: CurrentUiState.Companion.ListType
    ) {
        viewModelScope.launch {
            mutableUiState.update { it.copy(selectedItem = item, selectedType = type) }
            mediaListRepository.incrementOneProgress(
                entry = item.basicMediaListEntry,
                total = item.media?.basicMediaDetails?.duration()
            ).collectLatest { result ->
                mutableUiState.update {
                    if (result is DataResult.Success && result.data != null) {
                        onUpdateListEntry(result.data.basicMediaListEntry, type)
                    }
                    result.toUiState()
                }
            }
        }
    }

    override fun onUpdateListEntry(
        newListEntry: BasicMediaListEntry?,
        type: CurrentUiState.Companion.ListType
    ) {
        mutableUiState.value.run {
            selectedItem?.let { selectedItem ->
                if (selectedItem.basicMediaListEntry != newListEntry) {
                    val list = when (type) {
                        AIRING -> airingList
                        BEHIND -> behindList
                        ANIME -> animeList
                        MANGA -> mangaList
                    }
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
                                if (type == BEHIND
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

    override fun selectItem(item: CommonMediaListEntry, type: CurrentUiState.Companion.ListType) {
        mutableUiState.update { it.copy(selectedItem = item, selectedType = type) }
    }

    override fun toggleSetScoreDialog(open: Boolean) {
        mutableUiState.update { it.copy(openSetScoreDialog = open) }
    }

    override fun setScore(score: Double?) {
        viewModelScope.launch {
            mutableUiState.value.selectedItem?.let { item ->
                mediaListRepository.updateEntry(
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
                    status = MediaListStatus.CURRENT,
                    sort = listOf(MediaListSort.UPDATED_TIME_DESC),
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    page = 1
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
                    status = MediaListStatus.CURRENT,
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
    }
}