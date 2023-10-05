package com.axiel7.anihyou.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

abstract class UiStateViewModel<S : UiState<S>> : ViewModel() {

    protected abstract val mutableUiState: MutableStateFlow<S>
    abstract val uiState: StateFlow<S>

    fun <T> DataResult<T>.handleDataResult(onSuccess: (T) -> S?) {
        mutableUiState.update { it.setLoading(this is DataResult.Loading) }
        when (this) {
            is DataResult.Error -> mutableUiState.update { it.setError(message) }
            is DataResult.Success -> {
                onSuccess(data)?.let { data -> mutableUiState.update { data } }
            }

            else -> {}
        }
    }

    fun onErrorDisplayed() {
        mutableUiState.update { it.setError(null) }
    }

    fun <T> Flow<DataResult<T>>.dataResultStateInViewModel() =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DataResult.Loading)

    fun <T> Flow<T>.stateInViewModel(initialValue: T) =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initialValue)

    fun <T> Flow<T>.stateInViewModel() =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}