package com.axiel7.anihyou.feature.explore.recommendations

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.base.extensions.indexOfFirstOrNull
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.network.MediaRecommendationsQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.RecommendationRating
import com.axiel7.anihyou.core.network.type.RecommendationSort
import com.axiel7.anihyou.core.resources.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class RecommendationsViewModel(
    @InjectedParam private val isLoggedIn: Boolean,
    private val mediaRepository: MediaRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : PagedUiStateViewModel<RecommendationsUiState>(), RecommendationsEvent {

    override val initialState = RecommendationsUiState()

    override fun onVoteClick(
        recommendedMediaId: Int,
        baseMediaId: Int,
        recommendationId: Int,
        rating: RecommendationRating
    ) {
        if (!isLoggedIn) {
            mutableUiState.update { it.copy(errorId = R.string.not_logged_text) }
            return
        }

        val recommendations = uiState.value.recommendations
        val targetNode = recommendations.find { it.id == recommendationId } ?: return

        val previousUserRating = targetNode.userRating
        val newRating = if (previousUserRating == rating) RecommendationRating.NO_RATING else rating

        mediaRepository.saveRecommendation(
            mediaId = baseMediaId,
            mediaRecommendationId = recommendedMediaId,
            rating = newRating
        ).onEach { result ->
            if (result is DataResult.Success) {
                mutableUiState.update { state ->
                    val currentIndex = state.recommendations.indexOfFirst { it.id == recommendationId }
                    if (currentIndex != -1) {
                        val currentNode = state.recommendations[currentIndex]
                        state.recommendations[currentIndex] = currentNode.copy(
                            rating = result.data.SaveRecommendation?.rating ?: currentNode.rating,
                            userRating = result.data.SaveRecommendation?.userRating ?: newRating
                        )
                    }
                    state
                }
            } else if (result is DataResult.Error) {
                mutableUiState.update { state ->
                    state.copy(error = result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun refresh() {
        mutableUiState.update {
            it.copy(
                page = 1,
                hasNextPage = true,
                isLoading = true,
                fetchFromNetwork = true,
            )
        }
        viewModelScope.launch {
            // PullToRefresh needs a min delay when changing the isRefreshing state
            delay(1000.milliseconds)
            mutableUiState.update { it.copy(isLoading = false) }
        }
    }

    override fun onMyListChange(value: Boolean) {
        mutableUiState.update {
            it.copy(onMyList = value, page = 1, hasNextPage = true, isLoading = true)
        }
    }

    override fun onSortChange(value: RecommendationSort) {
        mutableUiState.update {
            it.copy(sort = value, page = 1, hasNextPage = true, isLoading = true)
        }
    }

    override fun clearErrorId() {
        mutableUiState.update {
            it.copy(errorId = null)
        }
    }

    override fun selectItem(
        details: BasicMediaDetails?,
        listEntry: BasicMediaListEntry?)
    {
        mutableUiState.update {
            it.copy(
                selectedMediaDetails = details,
                selectedMediaListEntry = listEntry,
            )
        }
    }

    init {
        uiState
            .filter { it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.onMyList == new.onMyList
                        && old.sort == new.sort
            }
            .flatMapLatest {
                mediaRepository.mediaRecommendations(
                    onList = if (it.onMyList) true else null, // onMyList false seems to be broken
                    sort = listOf(it.sort),
                    page = it.page,
                    perPage = 25,
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.recommendations.clear()
                        it.recommendations.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                            fetchFromNetwork = false,
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.blurAdult
            .onEach { value ->
                mutableUiState.update { it.copy(blurAdult = value) }
            }
            .launchIn(viewModelScope)
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        val selectedMediaId = uiState.value.selectedMediaDetails?.id ?: return
        mutableUiState.update { it.copy(selectedMediaListEntry = newListEntry) }

        val recommendations = uiState.value.recommendations
        recommendations.indexOfFirstOrNull { it.media?.basicMediaDetails?.id == selectedMediaId }
            ?.let { index ->
                val oldValue = recommendations[index]
                recommendations[index] = oldValue.copy(
                    media = oldValue.media?.copy(
                        mediaListEntry = newListEntry?.let { entry ->
                            oldValue.media?.mediaListEntry?.copy(basicMediaListEntry = entry)
                                ?: MediaRecommendationsQuery.MediaListEntry(
                                    __typename = "MediaListEntry",
                                    id = entry.id,
                                    mediaId = entry.mediaId,
                                    basicMediaListEntry = entry
                                )
                        }
                    )
                )
            }

        recommendations.indexOfFirstOrNull { it.mediaRecommendation?.basicMediaDetails?.id == selectedMediaId }
            ?.let { index ->
                val oldValue = recommendations[index]
                recommendations[index] = oldValue.copy(
                    mediaRecommendation = oldValue.mediaRecommendation?.copy(
                        mediaListEntry = newListEntry?.let { entry ->
                            oldValue.mediaRecommendation?.mediaListEntry?.copy(basicMediaListEntry = entry)
                                ?: MediaRecommendationsQuery.MediaListEntry1(
                                    __typename = "MediaListEntry",
                                    id = entry.id,
                                    mediaId = entry.mediaId,
                                    basicMediaListEntry = entry
                                )
                        }
                    )
                )
            }
    }
}
