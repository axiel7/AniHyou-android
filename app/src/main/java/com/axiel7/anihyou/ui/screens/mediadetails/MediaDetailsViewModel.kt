package com.axiel7.anihyou.ui.screens.mediadetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.stats.ScoreDistribution.Companion.asStat
import com.axiel7.anihyou.data.model.stats.StatusDistribution.Companion.asStat
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MediaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository,
    private val favoriteRepository: FavoriteRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<MediaDetailsUiState>() {

    val mediaId = savedStateHandle.getStateFlow<Int?>(MEDIA_ID_ARGUMENT.removeFirstAndLast(), null)

    override val mutableUiState = MutableStateFlow(MediaDetailsUiState())
    override val uiState = mutableUiState.asStateFlow()

    val accessToken = defaultPreferencesRepository.accessToken.stateInViewModel()

    init {
        mediaId
            .filterNotNull()
            .flatMapLatest { mediaId ->
                mediaRepository.getMediaDetails(mediaId)
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
    }

    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        if (mutableUiState.value.details?.mediaListEntry?.basicMediaListEntry != newListEntry) {
            mutableUiState.update { uiState ->
                uiState.copy(
                    details = uiState.details?.copy(
                        mediaListEntry = if (newListEntry != null)
                            uiState.details.mediaListEntry?.copy(basicMediaListEntry = newListEntry)
                        else null
                    )
                )
            }
        }
    }

    fun toggleFavorite() {
        mutableUiState.value.details?.let { details ->
            favoriteRepository.toggleFavorite(
                animeId = if (details.basicMediaDetails.type == MediaType.ANIME)
                    details.id else null,
                mangaId = if (details.basicMediaDetails.type == MediaType.MANGA)
                    details.id else null,
            ).onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success && result.data != null) {
                        it.copy(
                            details = it.details?.copy(isFavourite = !it.details.isFavourite)
                        )
                    } else {
                        it.copy(
                            error = (result as? DataResult.Error)?.message
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun fetchCharactersAndStaff() = mediaId
        .filterNotNull()
        .flatMapLatest { mediaId ->
            mediaRepository.getMediaCharactersAndStaff(mediaId)
        }
        .onEach { result ->
            mutableUiState.update {
                it.copy(
                    charactersAndStaff = (result as? DataResult.Success)?.data
                )
            }
        }
        .launchIn(viewModelScope)

    fun fetchRelationsAndRecommendations() = mediaId
        .filterNotNull()
        .flatMapLatest { mediaId ->
            mediaRepository.getMediaRelationsAndRecommendations(mediaId)
        }
        .onEach { result ->
            mutableUiState.update {
                it.copy(
                    relationsAndRecommendations = (result as? DataResult.Success)?.data
                )
            }
        }
        .launchIn(viewModelScope)

    fun fetchStats() = mediaId
        .filterNotNull()
        .flatMapLatest { mediaId ->
            mediaRepository.getMediaStats(mediaId)
        }
        .onEach { result ->
            mutableUiState.update { uiState ->
                uiState.copy(
                    isSuccessStats = result is DataResult.Success,
                    mediaStatusDistribution = (result as? DataResult.Success)
                        ?.data?.stats?.statusDistribution?.mapNotNull { it?.asStat() }.orEmpty(),
                    mediaScoreDistribution = (result as? DataResult.Success)
                        ?.data?.stats?.scoreDistribution?.mapNotNull { it?.asStat() }.orEmpty(),
                    mediaRankings = (result as? DataResult.Success)
                        ?.data?.rankings?.filterNotNull().orEmpty()
                )
            }
        }
        .launchIn(viewModelScope)

    fun fetchThreads() = mediaId
        .filterNotNull()
        .flatMapLatest { mediaId ->
            mediaRepository.getMediaThreadsPage(mediaId, page = 1)
        }
        .onEach { result ->
            mutableUiState.update { uiState ->
                uiState.copy(
                    isLoadingThreads = result is PagedResult.Loading,
                    threads = (result as? PagedResult.Success)?.list.orEmpty()
                )
            }
        }
        .launchIn(viewModelScope)

    fun fetchReviews() = mediaId
        .filterNotNull()
        .flatMapLatest { mediaId ->
            mediaRepository.getMediaReviewsPage(mediaId, page = 1)
        }
        .onEach { result ->
            mutableUiState.update { uiState ->
                uiState.copy(
                    isLoadingReviews = result is PagedResult.Loading,
                    reviews = (result as? PagedResult.Success)?.list.orEmpty()
                )
            }
        }
        .launchIn(viewModelScope)
}