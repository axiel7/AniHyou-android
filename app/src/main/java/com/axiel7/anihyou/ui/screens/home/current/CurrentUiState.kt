package com.axiel7.anihyou.ui.screens.home.current

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.state.UiState

@Stable
data class CurrentUiState(
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10_DECIMAL,
    val airingList: SnapshotStateList<CommonMediaListEntry> = mutableStateListOf(),
    val behindList: SnapshotStateList<CommonMediaListEntry> = mutableStateListOf(),
    val animeList: SnapshotStateList<CommonMediaListEntry> = mutableStateListOf(),
    val mangaList: SnapshotStateList<CommonMediaListEntry> = mutableStateListOf(),
    val selectedItem: CommonMediaListEntry? = null,
    val selectedType: CurrentListType? = null,
    val openSetScoreDialog: Boolean = false,
    val isLoadingPlusOne: Boolean = false,
    val fetchFromNetwork: Boolean = false,
    override val error: String? = null,
    override val isLoading: Boolean = true
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)

    private val hasAiring get() = airingList.isNotEmpty()

    private val hasBehind get() = behindList.isNotEmpty()

    private val hasAnime get() = animeList.isNotEmpty()

    private val hasManga get() = mangaList.isNotEmpty()

    val hasNothing get() = !hasAiring && !hasBehind && !hasAnime && !hasManga
}
