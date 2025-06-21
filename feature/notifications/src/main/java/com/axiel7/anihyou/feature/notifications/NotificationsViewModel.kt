package com.axiel7.anihyou.feature.notifications

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.domain.repository.NotificationRepository
import com.axiel7.anihyou.core.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModel(
    arguments: Routes.Notifications,
    private val notificationRepository: NotificationRepository,
) : PagedUiStateViewModel<NotificationsUiState>(), NotificationsEvent {

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
                    initialUnreadCount = arguments.unreadCount,
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