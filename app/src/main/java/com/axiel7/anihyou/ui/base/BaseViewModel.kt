package com.axiel7.anihyou.ui.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.PagedResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

abstract class BaseViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)

    fun <T> Flow<DataResult<T>>.dataResultStateInViewModel() =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DataResult.Loading)

    fun <T> Flow<PagedResult<T>>.pagedResultStateInViewModel() =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagedResult.Loading)
}