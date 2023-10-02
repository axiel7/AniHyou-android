package com.axiel7.anihyou.ui.screens.activity.publish

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class PublishActivityViewModel : BaseViewModel() {

    var wasPublished by mutableStateOf<Boolean?>(null)

    fun publishActivity(
        id: Int? = null,
        text: String
    ) = viewModelScope.launch(dispatcher) {
        ActivityRepository.updateTextActivity(
            id = id,
            text = text
        ).collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                wasPublished = result.data
            } else if (result is DataResult.Error) {
                wasPublished = false
                message = result.message
            }
        }
    }
}