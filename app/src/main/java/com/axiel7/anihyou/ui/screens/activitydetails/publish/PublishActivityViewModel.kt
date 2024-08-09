package com.axiel7.anihyou.ui.screens.activitydetails.publish

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublishActivityViewModel @Inject constructor(
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