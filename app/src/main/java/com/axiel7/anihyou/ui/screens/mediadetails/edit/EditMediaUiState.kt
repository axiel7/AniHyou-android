package com.axiel7.anihyou.ui.screens.mediadetails.edit

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.state.UiState
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
    val advancedScores: LinkedHashMap<String, Double>? = null,
    val startedAt: LocalDate? = null,
    val completedAt: LocalDate? = null,
    val repeatCount: Int? = null,
    val isPrivate: Boolean? = null,
    val notes: String? = null,
    val customLists: LinkedHashMap<String, Boolean>? = null,

    val openDatePicker: Boolean = false,
    val selectedDateType: Int = -1,
    val updateSuccess: Boolean = false,
    val openDeleteDialog: Boolean = false,
    val openCustomListsDialog: Boolean = false,

    override val error: String? = null,
    override val isLoading: Boolean = false,
) : UiState<EditMediaUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)

    val isNewEntry get() = listEntry == null

    fun mediaHasDuration(): Int? {
        val duration = mediaDetails?.duration()
        return if (duration != null && duration > 0) duration else null
    }

    fun mediaHasVolumes(): Int? {
        val volumes = mediaDetails?.volumes
        return if (volumes != null && volumes > 0) volumes else null
    }
}
