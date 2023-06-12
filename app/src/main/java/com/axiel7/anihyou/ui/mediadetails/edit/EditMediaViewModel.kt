package com.axiel7.anihyou.ui.mediadetails.edit

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.DeleteMediaListMutation
import com.axiel7.anihyou.UpdateEntryMutation
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.isManga
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.utils.DateUtils.toFuzzyDate
import com.axiel7.anihyou.utils.DateUtils.toFuzzyDateInput
import com.axiel7.anihyou.utils.DateUtils.toLocalDate
import kotlinx.coroutines.launch
import java.time.LocalDate

class EditMediaViewModel(
    private val mediaDetails: BasicMediaDetails,
    var listEntry: BasicMediaListEntry?
) : BaseViewModel() {

    val isNewEntry by derivedStateOf { listEntry == null }

    var status by mutableStateOf(listEntry?.status)
    var progress by mutableStateOf(listEntry?.progress)
    var volumeProgress by mutableStateOf(listEntry?.progressVolumes)
    var score by mutableStateOf(listEntry?.score)
    var startDate by mutableStateOf(listEntry?.startedAt?.fuzzyDate?.toLocalDate())
    var endDate by mutableStateOf(listEntry?.completedAt?.fuzzyDate?.toLocalDate())
    var repeatCount by mutableStateOf(listEntry?.repeat)
    var isPrivate by mutableStateOf(listEntry?.private)
    var notes by mutableStateOf(listEntry?.notes)

    fun onChangeStatus(value: MediaListStatus) {
        status = value
        if (isNewEntry && value == MediaListStatus.CURRENT) {
            startDate = LocalDate.now()
        }
        else if (value == MediaListStatus.COMPLETED) {
            endDate = LocalDate.now()
            mediaDetails.duration()?.let { if (it > 0) progress = it }
            if (mediaDetails.isManga()) {
                mediaDetails.volumes?.let { if (it > 0) volumeProgress = it }
            }
        }
    }

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

    fun onChangeVolumeProgress(value: Int?) {
        if (canChangeProgressTo(value, mediaDetails.volumes)) {
            volumeProgress = value
            if (status == MediaListStatus.PLANNING) {
                status = MediaListStatus.CURRENT
            }
        }
    }

    fun onChangeRepeatCount(value: Int?): Boolean {
        if (value != null && value >= 0) {
            repeatCount = value
            return true
        }
        return false
    }

    var openDatePicker by mutableStateOf(false)
    var selectedDateType by mutableIntStateOf(-1)

    var updateSuccess by mutableStateOf(false)

    suspend fun updateListEntry() {
        viewModelScope.launch {
            isLoading = true
            val response = UpdateEntryMutation(
                mediaId = Optional.present(mediaDetails.id),
                status = if (status != listEntry?.status) Optional.present(status)
                else Optional.absent(),
                score = if (score != listEntry?.score) Optional.present(score)
                else Optional.absent(),
                progress = if (progress != listEntry?.progress) Optional.present(progress)
                else Optional.absent(),
                progressVolumes = if (volumeProgress != listEntry?.progressVolumes) Optional.present(
                    volumeProgress
                )
                else Optional.absent(),
                startedAt = if (startDate?.toFuzzyDate() != listEntry?.startedAt?.fuzzyDate) Optional.present(
                    startDate?.toFuzzyDateInput()
                )
                else Optional.absent(),
                completedAt = if (endDate?.toFuzzyDate() != listEntry?.completedAt?.fuzzyDate) Optional.present(
                    endDate?.toFuzzyDateInput()
                )
                else Optional.absent(),
                repeat = if (repeatCount != listEntry?.repeat) Optional.present(repeatCount)
                else Optional.absent(),
                private = if (isPrivate != listEntry?.private) Optional.present(isPrivate)
                else Optional.absent(),
                notes = if (notes != listEntry?.notes) Optional.present(notes)
                else Optional.absent()
            ).tryMutation()

            response?.data?.SaveMediaListEntry?.basicMediaListEntry?.let { listEntry = it }
            updateSuccess = response != null

            isLoading = false
        }
    }

    var openDeleteDialog by mutableStateOf(false)

    suspend fun deleteListEntry() {
        viewModelScope.launch {
            if (listEntry == null) return@launch
            isLoading = true
            val response = DeleteMediaListMutation(
                mediaListEntryId = Optional.present(listEntry!!.id)
            ).tryMutation()

            if (response != null) listEntry = null
            updateSuccess = response != null

            isLoading = false
        }
    }
}