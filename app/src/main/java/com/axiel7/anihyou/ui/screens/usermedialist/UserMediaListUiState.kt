package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.ItemsPerRow
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.state.PagedUiState

@Stable
data class UserMediaListUiState(
    val mediaType: MediaType,
    val lists: MutableMap<String, List<CommonMediaListEntry>> = mutableMapOf(),
    val customLists: List<String> = emptyList(),
    val selectedListName: String? = null,
    val entries: SnapshotStateList<CommonMediaListEntry> = mutableStateListOf(),
    val status: MediaListStatus? = null,
    val isMyList: Boolean = true,
    val userId: Int? = null,
    var selectedItem: CommonMediaListEntry? = null,
    val sort: MediaListSort = MediaListSort.UPDATED_TIME_DESC,
    val listStyle: ListStyle = ListStyle.STANDARD,
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10,
    val isCompactScreen: Boolean = true,
    val itemsPerRow: ItemsPerRow = ItemsPerRow.DEFAULT,
    val fetchFromNetwork: Boolean = false,
    val sortMenuExpanded: Boolean = false,
    val openNotesDialog: Boolean = false,
    val plannedEntriesIds: List<Int> = emptyList(),
    val randomEntryId: Int? = null,
    override val page: Int = 1,
    override val hasNextPage: Boolean = false,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)

    fun getEntriesFromListName(name: String?) = if (name != null) {
        lists[name].orEmpty()
    } else {
        lists.values.flatten()
    }
}
