package com.axiel7.anihyou.ui.screens.activitydetails.publish

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.ui.common.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublishActivityViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : UiStateViewModel<PublishActivityUiState>() {

    override val mutableUiState = MutableStateFlow(PublishActivityUiState())
    override val uiState = mutableUiState.asStateFlow()

    fun publishActivity(
        id: Int? = null,
        text: String
    ) = viewModelScope.launch {
        activityRepository.updateTextActivity(
            id = id,
            text = text
        ).collect { result ->
            result.handleDataResult { data ->
                mutableUiState.updateAndGet { it.copy(wasPublished = data != null) }
            }
        }
    }

    fun publishActivityReply(
        activityId: Int,
        id: Int? = null,
        text: String
    ) = viewModelScope.launch {
        activityRepository.updateActivityReply(
            activityId = activityId,
            id = id,
            text = text
        ).collect { result ->
            result.handleDataResult { data ->
                mutableUiState.updateAndGet { it.copy(wasPublished = data != null) }
            }
        }
    }
}