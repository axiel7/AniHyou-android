package com.axiel7.anihyou.ui.screens.profile.stats

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.stats.LengthDistribution
import com.axiel7.anihyou.data.model.stats.ScoreDistribution
import com.axiel7.anihyou.data.model.stats.toOverviewStats
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
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

    fun setScoreType(value: ScoreDistribution.Type) =
        mutableUiState.update { it.copy(scoreType = value) }

    fun setLengthType(value: LengthDistribution.Type) =
        mutableUiState.update { it.copy(lengthType = value) }

    init {
        // anime stats
        mutableUiState
            .filter {
                it.mediaType == MediaType.ANIME
                        && it.type == UserStatType.OVERVIEW
                        && it.userId != null
            }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    userRepository.getOverviewAnimeStats(uiState.userId)
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            animeOverview = result.data?.toOverviewStats(),
                            isLoading = false
                        )
                    } else {
                        it.copy(isLoading = result is DataResult.Loading)
                    }
                }
            }
            .launchIn(viewModelScope)

        // manga stats
        mutableUiState
            .filter {
                it.mediaType == MediaType.MANGA
                        && it.type == UserStatType.OVERVIEW
                        && it.userId != null
            }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    userRepository.getOverviewMangaStats(uiState.userId)
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            mangaOverview = result.data?.toOverviewStats(),
                            isLoading = false
                        )
                    } else {
                        it.copy(isLoading = result is DataResult.Loading)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}