package com.axiel7.anihyou.ui.screens.thread

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.ThreadDetailsQuery
import com.axiel7.anihyou.data.model.thread.ChildComment
import com.axiel7.anihyou.ui.common.state.PagedUiState

@Stable
data class ThreadDetailsUiState(
    val details: ThreadDetailsQuery.Thread? = null,
    val comments: SnapshotStateList<ChildComment> = mutableStateListOf(),
    val isLiked: Boolean = false,
    val isSubscribed: Boolean = false,
    val fetchFromNetwork: Boolean = false,
    override val page: Int = 0,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
