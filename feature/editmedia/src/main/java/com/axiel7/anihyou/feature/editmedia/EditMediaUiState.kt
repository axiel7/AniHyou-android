package com.axiel7.anihyou.feature.editmedia

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.ui.common.state.UiState
import java.time.LocalDate

@Immutable
data class EditMediaUiState(
    val mediaDetails: BasicMediaDetails? = null,
    val listEntry: BasicMediaListEntry? = null,
    val scoreFormat: ScoreFormat = ScoreFormat.POINT_10_DECIMAL,
    val advancedScoringEnabled: Boolean = false,

    val status: MediaListStatus? = null,
    val progress: Int? = null,
    val volumeProgress: Int? = null,
    val score: Double? = null,
    val advancedScoresNames: List<String> = emptyList(),
    val advancedScores: SnapshotStateMap<String, Double> = mutableStateMapOf(),
    val startedAt: LocalDate? = null,
    val completedAt: LocalDate? = null,
    val repeatCount: Int? = null,
    val isPrivate: Boolean? = null,
    val isHiddenFromStatusLists: Boolean? = null,
    val notes: String? = null,
    val customLists: LinkedHashMap<String, Boolean>? = null,

    val openDatePicker: Boolean = false,
    val selectedDateType: Int = -1,
    val updateSuccess: Boolean = false,
    val openDeleteDialog: Boolean = false,
    val openCustomListsDialog: Boolean = false,

    override val error: String? = null,
    override val isLoading: Boolean = false,
) : UiState() {

    val isNewEntry = listEntry == null

    fun mediaHasDuration(): Int? {
        val duration = mediaDetails?.duration()
        return if (duration != null && duration > 0) duration else null
    }

    fun mediaHasVolumes(): Int? {
        val volumes = mediaDetails?.volumes
        return if (volumes != null && volumes > 0) volumes else null
    }

    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
