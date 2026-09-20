package com.axiel7.anihyou.feature.addrecommendation

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.network.SearchMediaQuery
import com.axiel7.anihyou.core.network.type.RecommendationRating
import com.axiel7.anihyou.core.ui.common.navigation.Route
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.InjectedParam

class AddRecommendationViewModel(
    @InjectedParam arguments: Route.AddRecommendation,
    private val mediaRepository: MediaRepository,
) : UiStateViewModel<AddRecommendationUiState>(), AddRecommendationEvent {

    override val initialState = AddRecommendationUiState(mediaId = arguments.mediaId)

    override fun insertRecommendation(media: SearchMediaQuery.Medium) {
        mutableUiState.update { it.copy(recommendation = media) }
    }

    override fun saveRecommendation(mediaId: Int, mediaRecommendationId: Int) {
        mutableUiState.update { it.copy(isLoading = true) }
        mediaRepository.saveRecommendation(
            mediaId = mediaId,
            mediaRecommendationId = mediaRecommendationId,
            rating = RecommendationRating.RATE_UP
        ).onEach { result ->
            if (result is DataResult.Success) {
                mutableUiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true,
                        returnedRecommendation = result.data
                    )
                }
            } else if (result is DataResult.Error) {
                result.toUiState()
            }
        }.launchIn(viewModelScope)
    }


    init {
        // should fetch it from Cache
        mediaRepository.getMediaDetails(mediaId = arguments.mediaId)
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            isLoading = false,
                            media = result.data
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}