package com.axiel7.anihyou.feature.mediadetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.core.common.DataResult
import com.axiel7.anihyou.core.common.PagedResult
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.model.stats.overview.StatusDistribution.Companion.asStat
import com.axiel7.anihyou.core.model.stats.overview.ScoreDistribution.Companion.asStat
import com.axiel7.anihyou.core.network.MediaDetailsQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.MediaCharacter
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.common.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class MediaDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository,
    private val favoriteRepository: FavoriteRepository,
) : UiStateViewModel<MediaDetailsUiState>(), MediaDetailsEvent {

    private val arguments = savedStateHandle.toRoute<Routes.MediaDetails>()

    override val initialState = MediaDetailsUiState()

    fun setIsLoggedIn(value: Boolean) {
        mutableUiState.update { it.copy(isLoggedIn = value) }
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        if (mutableUiState.value.details?.mediaListEntry?.basicMediaListEntry != newListEntry) {
            mutableUiState.update { uiState ->
                uiState.copy(
                    details = uiState.details?.copy(
                        mediaListEntry = if (newListEntry != null) {
                            uiState.details.mediaListEntry?.copy(basicMediaListEntry = newListEntry)
                                ?: MediaDetailsQuery.MediaListEntry(
                                    __typename = "MediaDetailsQuery.MediaListEntry",
                                    startedAt = null,
                                    completedAt = null,
                                    id = newListEntry.id,
                                    mediaId = uiState.details.id,
                                    basicMediaListEntry = newListEntry,
                                )
                        }
                        else null
                    )
                )
            }
        }
    }

    override fun toggleFavorite() {
        mutableUiState.value.details?.let { details ->
            favoriteRepository.toggleFavorite(
                animeId = if (details.basicMediaDetails.type == MediaType.ANIME)
                    details.id else null,
                mangaId = if (details.basicMediaDetails.type == MediaType.MANGA)
                    details.id else null,
            ).onEach { result ->
                mutableUiState.update { state ->
                    if (result is DataResult.Success && result.data != null) {
                        val newDetails = state.details
                            ?.copy(isFavourite = !state.details.isFavourite)
                            ?.also {
                                mediaRepository.updateMediaDetailsCache(it)
                            }
                        state.copy(
                            details = newDetails
                        )
                    } else {
                        state.copy(
                            error = (result as? DataResult.Error)?.message
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchCharactersAndStaff() {
        mediaRepository.getMediaCharactersAndStaff(mediaId = arguments.id)
            .onEach { result ->
                if (result is DataResult.Success) {
                    mutableUiState.update { uiState ->
                        uiState.copy(
                            staff = result.data.staff.map { it.mediaStaff },
                            characters = result.data.characters.map { it.mediaCharacter }
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun fetchRelationsAndRecommendations() {
        mediaRepository.getMediaRelationsAndRecommendations(mediaId = arguments.id)
            .onEach { result ->
                if (result is DataResult.Success) {
                    mutableUiState.update {
                        it.copy(
                            relationsAndRecommendations = result.data
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun fetchStats() {
        viewModelScope.launch {
            mediaRepository.getMediaStats(mediaId = arguments.id)
                .collectLatest { result ->
                    if (result is DataResult.Success) {
                        mutableUiState.update { uiState ->
                            uiState.copy(
                                isSuccessStats = true,
                                mediaStatusDistribution = result.data?.stats?.statusDistribution
                                    ?.mapNotNull { it?.asStat() }.orEmpty(),
                                mediaScoreDistribution = result.data?.stats?.scoreDistribution
                                    ?.mapNotNull { it?.asStat() }.orEmpty(),
                                mediaRankings = result.data?.rankings?.filterNotNull().orEmpty()
                            )
                        }
                    }
                }

            mediaRepository.getMediaFollowing(mediaId = arguments.id, page = 1)
                .collectLatest { result ->
                    if (result is PagedResult.Success) {
                        mutableUiState.update { uiState ->
                            uiState.copy(following = result.list)
                        }
                    }
                }
        }
    }

    override fun fetchThreads() {
        mediaRepository.getMediaThreadsPage(mediaId = arguments.id, page = 1)
            .onEach { result ->
                mutableUiState.update { uiState ->
                    if (result is PagedResult.Success) {
                        uiState.copy(
                            isLoadingThreads = false,
                            threads = result.list
                        )
                    } else {
                        uiState.copy(
                            isLoadingThreads = result is PagedResult.Loading,
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun fetchReviews() {
        mediaRepository.getMediaReviewsPage(mediaId = arguments.id, page = 1)
            .onEach { result ->
                mutableUiState.update { uiState ->
                    if (result is PagedResult.Success) {
                        uiState.copy(
                            isLoadingReviews = false,
                            reviews = result.list
                        )
                    } else {
                        uiState.copy(
                            isLoadingThreads = result is PagedResult.Loading,
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun fetchActivity() {
        mediaRepository.getMediaActivityPage(mediaId = arguments.id, page = 1)
            .onEach { result ->
                mutableUiState.update { uiState ->
                    if (result is PagedResult.Success) {
                        uiState.copy(
                            isLoadingActivity = false,
                            activity = result.list
                        )
                    } else {
                        uiState.copy(
                            isLoadingActivity = result is PagedResult.Loading,
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun showVoiceActorsSheet(character: MediaCharacter) {
        mutableUiState.update { uiState ->
            uiState.copy(
                selectedCharacterVoiceActors = character.voiceActors?.mapNotNull { it?.commonVoiceActor },
                showVoiceActorsSheet = true
            )
        }
    }

    override fun hideVoiceActorSheet() {
        mutableUiState.update { it.copy(showVoiceActorsSheet = false) }
    }

    private suspend fun fetchAnimeThemes(idMal: Int) {
        mediaRepository.getAnimeThemes(idMal = idMal)?.let {
            mutableUiState.update { state ->
                state.copy(
                    openings = it.openingThemes.orEmpty(),
                    endings = it.endingThemes.orEmpty(),
                )
            }
        }
    }

    init {
        mediaRepository.getMediaDetails(mediaId = arguments.id)
            .onEach { result ->
                mutableUiState.updateAndGet {
                    if (result is DataResult.Success) {
                        it.copy(
                            isLoading = false,
                            details = result.data
                        )
                    } else {
                        result.toUiState()
                    }
                }.also {
                    it.details?.idMal?.let { idMal ->
                        if (it.details.basicMediaDetails.type == MediaType.ANIME)
                            fetchAnimeThemes(idMal)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}