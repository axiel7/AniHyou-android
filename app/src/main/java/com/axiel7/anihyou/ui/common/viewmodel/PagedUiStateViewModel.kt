package com.axiel7.anihyou.ui.common.viewmodel

import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.ui.common.event.PagedEvent
import com.axiel7.anihyou.ui.common.state.PagedUiState
import kotlinx.coroutines.flow.update

abstract class PagedUiStateViewModel<S : PagedUiState<S>> : UiStateViewModel<S>(), PagedEvent {

    protected fun <D> PagedResult<D>.toUiState(): S {
        return when (this) {
            is PagedResult.Loading -> mutableUiState.value.setLoading(true)

            is PagedResult.Error -> mutableUiState.value.setError(message).setLoading(false)

            is PagedResult.Success -> mutableUiState.value.setLoading(false)
        }
    }

    protected fun <D> PagedResult<D>.toUiState(loadingWhen: Boolean): S {
        return when (this) {
            is PagedResult.Loading -> mutableUiState.value.setLoading(loadingWhen)

            is PagedResult.Error -> mutableUiState.value.setError(message).setLoading(false)

            is PagedResult.Success -> mutableUiState.value.setLoading(false)
        }
    }

    fun setHasNextPage(value: Boolean) = mutableUiState.update { it.setHasNextPage(value) }

    override fun onLoadMore() {
        if (uiState.value.hasNextPage && !uiState.value.isLoading) {
            mutableUiState.update { it.setPage(it.page + 1) }
        }
    }
}