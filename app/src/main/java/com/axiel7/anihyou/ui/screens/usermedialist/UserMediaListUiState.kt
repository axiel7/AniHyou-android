package com.axiel7.anihyou.ui.screens.usermedialist

import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.ItemsPerRow
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.state.PagedUiState

data class UserMediaListUiState(
    val mediaType: MediaType,
    val isMyList: Boolean = true,
    val userId: Int? = null,
    var selectedItem: UserMediaListQuery.MediaList? = null,
    val status: MediaListStatus = MediaListStatus.CURRENT,
    val sort: MediaListSort = MediaListSort.UPDATED_TIME_DESC,
    val listStyle: ListStyle = ListStyle.STANDARD,
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10,
    val isCompactScreen: Boolean = true,
    val itemsPerRow: ItemsPerRow = ItemsPerRow.DEFAULT,
    val fetchFromNetwork: Boolean = false,
    val sortMenuExpanded: Boolean = false,
    val openNotesDialog: Boolean = false,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState<UserMediaListUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
