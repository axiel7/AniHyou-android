package com.axiel7.anihyou.ui.screens.activitydetails.publish

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublishActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : UiStateViewModel<PublishActivityUiState>(), PublishActivityEvent {

    override val mutableUiState = MutableStateFlow(PublishActivityUiState())
    override val uiState = mutableUiState.asStateFlow()

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