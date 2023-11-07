package com.axiel7.anihyou.ui.screens.profile.stats

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.stats.StatDistributionType
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserStatsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : UiStateViewModel<UserStatsUiState>() {

    override val mutableUiState = MutableStateFlow(UserStatsUiState())
    override val uiState = mutableUiState.asStateFlow()

    fun setUserId(value: Int) = mutableUiState.update { it.copy(userId = value) }

    fun setType(value: UserStatType) = mutableUiState.update { it.copy(type = value) }

    fun setMediaType(value: MediaType) = mutableUiState.update { it.copy(mediaType = value) }

    fun setScoreType(value: StatDistributionType) =
        mutableUiState.update { it.copy(scoreType = value) }

    fun setLengthType(value: StatDistributionType) =
        mutableUiState.update { it.copy(lengthType = value) }

    fun setReleaseYearType(value: StatDistributionType) =
        mutableUiState.update { it.copy(releaseYearType = value) }

    fun setStartYearType(value: StatDistributionType) =
        mutableUiState.update { it.copy(startYearType = value) }

    fun setGenresType(value: StatDistributionType) =
        mutableUiState.update { it.copy(genresType = value) }

    fun setTagsType(value: StatDistributionType) =
        mutableUiState.update { it.copy(tagsType = value) }

    init {
        // overview
        mutableUiState
            .filter {
                it.type == UserStatType.OVERVIEW
                        && it.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.mediaType == new.mediaType
                        && old.userId == new.userId
            }
            .flatMapLatest { uiState ->
                userRepository.getOverviewStats(
                    userId = uiState.userId!!,
                    mediaType = uiState.mediaType
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        when (it.mediaType) {
                            MediaType.ANIME ->
                                it.copy(
                                    animeOverview = result.data,
                                    isLoading = false
                                )

                            MediaType.MANGA ->
                                it.copy(
                                    mangaOverview = result.data,
                                    isLoading = false
                                )

                            else -> it.copy(isLoading = false)
                        }
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)

        // genres
        mutableUiState
            .filter {
                it.type == UserStatType.GENRES
                        && it.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.genresType == new.genresType
                        && old.mediaType == new.mediaType
                        && old.userId == new.userId
            }
            .flatMapLatest { uiState ->
                userRepository.getGenresStats(
                    userId = uiState.userId!!,
                    mediaType = uiState.mediaType,
                    sort = uiState.genresType.userStatisticsSort(ascending = false)
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        when (it.mediaType) {
                            MediaType.ANIME ->
                                it.copy(
                                    animeGenres = result.data,
                                    isLoading = false
                                )

                            MediaType.MANGA ->
                                it.copy(
                                    mangaGenres = result.data,
                                    isLoading = false
                                )

                            else -> it.copy(isLoading = false)
                        }
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)

        // tags
        mutableUiState
            .filter {
                it.type == UserStatType.TAGS
                        && it.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.tagsType == new.tagsType
                        && old.mediaType == new.mediaType
                        && old.userId == new.userId
            }
            .flatMapLatest { uiState ->
                userRepository.getTagsStats(
                    userId = uiState.userId!!,
                    mediaType = uiState.mediaType,
                    sort = uiState.tagsType.userStatisticsSort(ascending = false)
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        when (it.mediaType) {
                            MediaType.ANIME ->
                                it.copy(
                                    animeTags = result.data,
                                    isLoading = false
                                )

                            MediaType.MANGA ->
                                it.copy(
                                    mangaTags = result.data,
                                    isLoading = false
                                )

                            else -> it.copy(isLoading = false)
                        }
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}