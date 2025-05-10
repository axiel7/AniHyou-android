package com.axiel7.anihyou.feature.activitydetails.publish

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.domain.repository.ActivityRepository
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PublishActivityViewModel(
    private val activityRepository: ActivityRepository
) : UiStateViewModel<PublishActivityUiState>(), PublishActivityEvent {

    override val initialState = PublishActivityUiState()

    override fun publishActivity(id: Int?, text: String) {
        viewModelScope.launch {
            activityRepository.updateTextActivity(
                id = id,
                text = text
            ).collect { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            isLoading = false,
                            wasPublished = result.data != null
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
        }
    }

    override fun publishActivityReply(
        activityId: Int,
        id: Int?,
        text: String
    ) {
        viewModelScope.launch {
            activityRepository.updateActivityReply(
                activityId = activityId,
                id = id,
                text = text
            ).collect { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            isLoading = false,
                            wasPublished = result.data != null
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
        }
    }
}