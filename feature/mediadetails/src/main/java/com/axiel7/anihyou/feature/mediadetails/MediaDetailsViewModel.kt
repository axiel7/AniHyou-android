package com.axiel7.anihyou.feature.mediadetails

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.model.stats.overview.ScoreDistribution.Companion.asStat
import com.axiel7.anihyou.core.model.stats.overview.StatusDistribution.Companion.asStat
import com.axiel7.anihyou.core.network.MediaDetailsQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.MediaCharacter
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.RecommendationRating
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

class MediaDetailsViewModel(
    @InjectedParam private val arguments: Routes.MediaDetails,
    defaultPreferencesRepository: DefaultPreferencesRepository,
    private val mediaRepository: MediaRepository,
    private val favoriteRepository: FavoriteRepository,
) : UiStateViewModel<MediaDetailsUiState>(), MediaDetailsEvent {

    override val initialState = MediaDetailsUiState(isLoggedIn = arguments.isLoggedIn)

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
            viewModelScope.launch {
                favoriteRepository.toggleFavorite(
                    animeId = if (details.basicMediaDetails.type == MediaType.ANIME)
                        details.id else null,
                    mangaId = if (details.basicMediaDetails.type == MediaType.MANGA)
                        details.id else null,
                ).let { result ->
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
                }
            }
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

    override fun onVoteClick(recommendedMediaId: Int, recommendationId: Int, rating: RecommendationRating) {
        if (!arguments.isLoggedIn) {
            mutableUiState.update { it.copy(errorId = R.string.not_logged_text) }
            return
        }

        val recommendations = mutableUiState.value.relationsAndRecommendations?.recommendations
        val targetNode = recommendations?.find { it.mediaRecommended.id == recommendationId } ?: return

        val previousUserRating = targetNode.mediaRecommended.userRating
        val newRating = if (previousUserRating == rating) RecommendationRating.NO_RATING else rating // if the new rating is the same as the old one remove the rating

        mediaRepository.saveRecommendation(
            mediaId = arguments.id, // base media id
            mediaRecommendationId = recommendedMediaId, // id of the media which gets recommended
            rating = newRating
        ).onEach { result ->
            if (result is DataResult.Success) {
                mutableUiState.update { state ->
                    val relAndRecs = state.relationsAndRecommendations ?: return@update state
                    val updatedRecs = relAndRecs.recommendations.map { node ->
                        if (node.mediaRecommended.id == recommendationId) {
                            node.copy(
                                mediaRecommended = node.mediaRecommended.copy(
                                    rating = result.data.SaveRecommendation?.rating
                                        ?: node.mediaRecommended.rating,
                                    userRating = result.data.SaveRecommendation?.userRating
                                        ?: newRating
                                )
                            )
                        } else node
                    }
                    state.copy(relationsAndRecommendations = relAndRecs.copy(recommendations = updatedRecs))
                }
            } else if (result is DataResult.Error) {
                mutableUiState.update { state ->
                    return@update state.copy(error = result.message)
                }
            }
        }.launchIn(viewModelScope)
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

        defaultPreferencesRepository.translatorApp
            .onEach { value ->
                mutableUiState.update { it.copy(translatorApp = value) }
            }
            .launchIn(viewModelScope)
    }
}