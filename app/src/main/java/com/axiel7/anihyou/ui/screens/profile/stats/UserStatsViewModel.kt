package com.axiel7.anihyou.ui.screens.profile.stats

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.model.stats.StatDistributionType
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
) : UiStateViewModel<UserStatsUiState>(), UserStatsEvent {

    override val initialState = UserStatsUiState()

    fun setUserId(value: Int) = mutableUiState.update { it.copy(userId = value) }

    override fun setType(value: UserStatType) = mutableUiState.update { it.copy(type = value) }

    override fun setMediaType(value: MediaType) =
        mutableUiState.update { it.copy(mediaType = value) }

    override fun setScoreType(value: StatDistributionType) =
        mutableUiState.update { it.copy(scoreType = value) }

    override fun setLengthType(value: StatDistributionType) =
        mutableUiState.update { it.copy(lengthType = value) }

    override fun setReleaseYearType(value: StatDistributionType) =
        mutableUiState.update { it.copy(releaseYearType = value) }

    override fun setStartYearType(value: StatDistributionType) =
        mutableUiState.update { it.copy(startYearType = value) }

    override fun setGenresType(value: StatDistributionType) =
        mutableUiState.update { it.copy(genresType = value) }

    override fun setTagsType(value: StatDistributionType) =
        mutableUiState.update { it.copy(tagsType = value) }

    override fun setStaffType(value: StatDistributionType) =
        mutableUiState.update { it.copy(staffType = value) }

    override fun setVoiceActorsType(value: StatDistributionType) =
        mutableUiState.update { it.copy(voiceActorsType = value) }

    override fun setStudiosType(value: StatDistributionType) =
        mutableUiState.update { it.copy(studiosType = value) }

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

        // staff
        mutableUiState
            .filter {
                it.type == UserStatType.STAFF
                        && it.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.staffType == new.staffType
                        && old.mediaType == new.mediaType
                        && old.userId == new.userId
            }
            .flatMapLatest { uiState ->
                userRepository.getStaffStats(
                    userId = uiState.userId!!,
                    mediaType = uiState.mediaType,
                    sort = uiState.staffType.userStatisticsSort(ascending = false)
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        when (it.mediaType) {
                            MediaType.ANIME ->
                                it.copy(
                                    animeStaff = result.data,
                                    isLoading = false
                                )

                            MediaType.MANGA ->
                                it.copy(
                                    mangaStaff = result.data,
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

        // voice actors
        mutableUiState
            .filter {
                it.type == UserStatType.VOICE_ACTORS
                        && it.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.voiceActorsType == new.voiceActorsType
                        && old.userId == new.userId
            }
            .flatMapLatest { uiState ->
                userRepository.getVoiceActorsStats(
                    userId = uiState.userId!!,
                    sort = uiState.voiceActorsType.userStatisticsSort(ascending = false)
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            voiceActors = result.data,
                            isLoading = false
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)

        // studios
        mutableUiState
            .filter {
                it.type == UserStatType.STUDIOS
                        && it.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.studiosType == new.studiosType
                        && old.userId == new.userId
            }
            .flatMapLatest { uiState ->
                userRepository.getStudiosStats(
                    userId = uiState.userId!!,
                    sort = uiState.studiosType.userStatisticsSort(ascending = false)
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            studios = result.data,
                            isLoading = false
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}