package com.axiel7.anihyou.ui.screens.mediadetails.edit

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.isManga
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.MediaListRepository
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.utils.DateUtils.millisToLocalDate
import com.axiel7.anihyou.utils.DateUtils.toFuzzyDate
import com.axiel7.anihyou.utils.DateUtils.toLocalDate
import kotlinx.coroutines.launch
import java.time.LocalDate

class EditMediaViewModel(
    private val mediaDetails: BasicMediaDetails,
    var listEntry: BasicMediaListEntry?
) : BaseViewModel() {

    val isNewEntry by derivedStateOf { listEntry == null }

    var status by mutableStateOf(listEntry?.status)
        private set

    fun onChangeStatus(value: MediaListStatus) {
        status = value
        if (isNewEntry && value == MediaListStatus.CURRENT) {
            startDate = LocalDate.now()
        } else if (value == MediaListStatus.COMPLETED) {
            endDate = LocalDate.now()
            mediaDetails.duration()?.let { if (it > 0) progress = it }
            if (mediaDetails.isManga()) {
                mediaDetails.volumes?.let { if (it > 0) volumeProgress = it }
            }
        }
    }

    var progress by mutableStateOf(listEntry?.progress)
        private set

    fun onChangeProgress(value: Int?) {
        if (canChangeProgressTo(value, mediaDetails.duration())) {
            progress = value
            if (status == MediaListStatus.PLANNING) {
                status = MediaListStatus.CURRENT
            }
        }
    }

    private fun canChangeProgressTo(value: Int?, limit: Int?) = when {
        value == null -> true //allow to set empty
        value < 0 -> false //progress must be positive
        value == 0 -> true //allow set to 0
        limit == null -> true //no limitations
        limit <= 0 -> true //no limitations
        value <= limit -> true //progress must be below total
        else -> false
    }

    var volumeProgress by mutableStateOf(listEntry?.progressVolumes)
        private set

    fun onChangeVolumeProgress(value: Int?) {
        if (canChangeProgressTo(value, mediaDetails.volumes)) {
            volumeProgress = value
            if (status == MediaListStatus.PLANNING) {
                status = MediaListStatus.CURRENT
            }
        }
    }

    var score by mutableStateOf(listEntry?.score)
        private set

    fun onChangeScore(value: Double) {
        score = value
    }

    var startDate by mutableStateOf(listEntry?.startedAt?.fuzzyDate?.toLocalDate())
        private set

    fun onChangeStartDate(value: Long?) {
        startDate = value?.millisToLocalDate()
    }

    var endDate by mutableStateOf(listEntry?.completedAt?.fuzzyDate?.toLocalDate())
        private set

    fun onChangeEndDate(value: Long?) {
        endDate = value?.millisToLocalDate()
    }

    var repeatCount by mutableStateOf(listEntry?.repeat)
        private set

    fun onChangeRepeatCount(value: Int?): Boolean {
        if (value != null && value >= 0) {
            repeatCount = value
            return true
        }
        return false
    }

    var isPrivate by mutableStateOf(listEntry?.private)
        private set

    fun onChangeIsPrivate(value: Boolean) {
        isPrivate = value
    }

    var notes by mutableStateOf(listEntry?.notes)
        private set

    fun onChangeNotes(value: String) {
        notes = value
    }

    var openDatePicker by mutableStateOf(false)
    var selectedDateType by mutableIntStateOf(-1)

    var updateSuccess by mutableStateOf(false)

    fun updateListEntry() = viewModelScope.launch(dispatcher) {
        MediaListRepository.updateEntry(
            oldEntry = listEntry,
            mediaId = mediaDetails.id,
            status = status,
            score = score,
            progress = progress,
            progressVolumes = volumeProgress,
            startedAt = startDate?.toFuzzyDate(),
            completedAt = endDate?.toFuzzyDate(),
            repeat = repeatCount,
            private = isPrivate,
            notes = notes,
        ).collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                listEntry = result.data
                updateSuccess = true
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    var openDeleteDialog by mutableStateOf(false)

    fun deleteListEntry() = viewModelScope.launch(dispatcher) {
        if (listEntry == null) return@launch
        MediaListRepository.deleteEntry(listEntry!!.id).collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                if (result.data) {
                    listEntry = null
                    updateSuccess = true
                }
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

}