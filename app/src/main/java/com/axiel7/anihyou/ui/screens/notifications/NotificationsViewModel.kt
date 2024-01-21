package com.axiel7.anihyou.ui.screens.notifications

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.data.repository.NotificationRepository
import com.axiel7.anihyou.ui.common.navigation.NavArgument
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val notificationRepository: NotificationRepository,
) : PagedUiStateViewModel<NotificationsUiState>(), NotificationsEvent {

    private val initialUnreadCount: Int = savedStateHandle[NavArgument.UnreadCount.name] ?: 0

    override val initialState = NotificationsUiState()

    private var resetCount = true

    override fun setType(value: NotificationTypeGroup) {
        mutableUiState.update {
            it.copy(type = value, page = 1, hasNextPage = true)
        }
    }

    init {
        mutableUiState
            .filter { it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.type == new.type
            }
            .flatMapLatest { uiState ->
                notificationRepository.getNotificationsPage(
                    type = uiState.type,
                    resetCount = resetCount,
                    initialUnreadCount = initialUnreadCount,
                    page = uiState.page
                ).also {
                    resetCount = false // only reset on first call
                }
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.notifications.clear()
                        it.notifications.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            isLoading = false
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}