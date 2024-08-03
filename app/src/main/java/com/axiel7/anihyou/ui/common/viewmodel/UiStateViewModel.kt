package com.axiel7.anihyou.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.ui.common.event.UiEvent
import com.axiel7.anihyou.ui.common.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Suppress("UNCHECKED_CAST")
abstract class UiStateViewModel<S : UiState> : ViewModel(), UiEvent {

    protected abstract val initialState: S
    protected val mutableUiState by lazy { MutableStateFlow(initialState) }
    val uiState: StateFlow<S> by lazy { mutableUiState.asStateFlow() }

    protected fun <D> DataResult<D>.toUiState(): S {
        return when (this) {
            is DataResult.Loading -> mutableUiState.value.setLoading(true) as S

            is DataResult.Error -> mutableUiState.value.setError(message).setLoading(false) as S

            is DataResult.Success -> mutableUiState.value.setLoading(false) as S
        }
    }

    override fun onErrorDisplayed() {
        mutableUiState.update { it.removeError() as S }
    }
}