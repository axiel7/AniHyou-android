package com.axiel7.anihyou.ui.screens.notifications

import com.axiel7.anihyou.data.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.ui.common.state.PagedUiState

data class NotificationsUiState(
    val type: NotificationTypeGroup = NotificationTypeGroup.ALL,
    override val page: Int = 0,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState<NotificationsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
