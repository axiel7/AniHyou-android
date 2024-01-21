package com.axiel7.anihyou.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.ui.common.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

abstract class UiStateViewModel<S : UiState<S>> : ViewModel() {

    protected abstract val mutableUiState: MutableStateFlow<S>
    abstract val uiState: StateFlow<S>

    protected fun <D> DataResult<D>.toUiState(): S {
        return when (this) {
            is DataResult.Loading -> mutableUiState.value.setLoading(true)

            is DataResult.Error -> mutableUiState.value.setError(message).setLoading(false)

            is DataResult.Success -> mutableUiState.value.setLoading(false)
        }
    }

    fun onErrorDisplayed() {
        mutableUiState.update { it.setError(null) }
    }
}