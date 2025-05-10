package com.axiel7.anihyou.feature.usermedialist

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.model.ItemsPerRow
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.base.state.PagedUiState

@Stable
data class UserMediaListUiState(
    val mediaType: MediaType,
    val lists: MutableMap<String, List<CommonMediaListEntry>> = mutableMapOf(),
    val orderedListNames: List<String> = emptyList(),
    val selectedListName: String? = null,
    val entries: SnapshotStateList<CommonMediaListEntry> = mutableStateListOf(),
    val status: MediaListStatus? = null,
    val isMyList: Boolean = true,
    val userId: Int? = null,
    val selectedItem: CommonMediaListEntry? = null,
    val sort: MediaListSort = MediaListSort.UPDATED_TIME_DESC,
    val listStyle: ListStyle = ListStyle.STANDARD,
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10,
    val isCompactScreen: Boolean = true,
    val itemsPerRow: ItemsPerRow = ItemsPerRow.DEFAULT,
    val fetchFromNetwork: Boolean = false,
    val sortMenuExpanded: Boolean = false,
    val openNotesDialog: Boolean = false,
    val openSetScoreDialog: Boolean = false,
    val plannedEntriesIds: List<Int> = emptyList(),
    val randomEntryId: Int? = null,
    val isLoadingPlusOne: Boolean = false,
    override val page: Int = 1,
    override val hasNextPage: Boolean = false,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
