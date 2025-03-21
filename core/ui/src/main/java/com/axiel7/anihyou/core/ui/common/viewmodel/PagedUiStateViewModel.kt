package com.axiel7.anihyou.core.ui.common.viewmodel

import com.axiel7.anihyou.core.common.PagedResult
import com.axiel7.anihyou.core.ui.common.event.PagedEvent
import com.axiel7.anihyou.core.ui.common.state.PagedUiState
import kotlinx.coroutines.flow.update

@Suppress("UNCHECKED_CAST")
abstract class PagedUiStateViewModel<S : PagedUiState> : UiStateViewModel<S>(), PagedEvent {

    protected fun <D> PagedResult<D>.toUiState(): S {
        return when (this) {
            is PagedResult.Loading -> mutableUiState.value.setLoading(true) as S

            is PagedResult.Error -> mutableUiState.value.setError(message).setLoading(false) as S

            is PagedResult.Success -> mutableUiState.value.setLoading(false) as S
        }
    }

    protected fun <D> PagedResult<D>.toUiState(loadingWhen: Boolean): S {
        return when (this) {
            is PagedResult.Loading -> mutableUiState.value.setLoading(loadingWhen) as S

            is PagedResult.Error -> mutableUiState.value.setError(message).setLoading(false) as S

            is PagedResult.Success -> mutableUiState.value.setLoading(false) as S
        }
    }

    fun setHasNextPage(value: Boolean) = mutableUiState.update { it.setHasNextPage(value) as S }

    override fun onLoadMore() {
        if (uiState.value.hasNextPage && !uiState.value.isLoading) {
            mutableUiState.update { it.setPage(it.page + 1) as S }
        }
    }
}