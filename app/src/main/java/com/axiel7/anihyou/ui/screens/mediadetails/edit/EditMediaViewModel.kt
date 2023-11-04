package com.axiel7.anihyou.ui.screens.mediadetails.edit

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaListRepository
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.utils.DateUtils.millisToLocalDate
import com.axiel7.anihyou.utils.DateUtils.toFuzzyDate
import com.axiel7.anihyou.utils.DateUtils.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EditMediaViewModel @Inject constructor(
    private val mediaListRepository: MediaListRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<EditMediaUiState>() {

    override val mutableUiState = MutableStateFlow(EditMediaUiState())
    override val uiState = mutableUiState.asStateFlow()

    val scoreFormat = defaultPreferencesRepository.scoreFormat
        .stateInViewModel()

    val userId = defaultPreferencesRepository.userId
        .stateInViewModel()

    fun setMediaDetails(value: BasicMediaDetails) =
        mutableUiState.update { it.copy(mediaDetails = value) }

    fun setListEntry(value: BasicMediaListEntry?) = mutableUiState.update {
        it.copy(
            listEntry = value,
            status = value?.status,
            progress = value?.progress,
            volumeProgress = value?.progressVolumes,
            score = value?.score,
            startedAt = value?.startedAt?.fuzzyDate?.toLocalDate(),
            completedAt = value?.completedAt?.fuzzyDate?.toLocalDate(),
            repeatCount = value?.repeat,
            isPrivate = value?.private,
            notes = value?.notes,
        )
    }

    fun onChangeStatus(value: MediaListStatus) {
        if (mutableUiState.value.isNewEntry && value == MediaListStatus.CURRENT) {
            mutableUiState.update {
                it.copy(
                    status = value,
                    startedAt = LocalDate.now()
                )
            }
        } else if (value == MediaListStatus.COMPLETED) {
            mutableUiState.update { uiState ->
                uiState.copy(
                    status = value,
                    progress = uiState.mediaHasDuration() ?: uiState.progress,
                    volumeProgress = uiState.mediaHasVolumes() ?: uiState.volumeProgress,
                    completedAt = LocalDate.now()
                )
            }
        } else {
            mutableUiState.update { it.copy(status = value) }
        }
    }

    fun onChangeProgress(value: Int?) {
        if (canChangeProgressTo(value, uiState.value.mediaDetails?.duration())) {
            mutableUiState.update {
                it.copy(
                    progress = value,
                    status = if (it.status == MediaListStatus.PLANNING) MediaListStatus.CURRENT
                    else it.status
                )
            }
        }
    }

    fun onChangeVolumeProgress(value: Int?) {
        if (canChangeProgressTo(value, uiState.value.mediaDetails?.volumes)) {
            mutableUiState.update {
                it.copy(
                    volumeProgress = value,
                    status = if (it.status == MediaListStatus.PLANNING) MediaListStatus.CURRENT
                    else it.status
                )
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

    fun onChangeScore(value: Double) {
        mutableUiState.update { it.copy(score = value) }
    }

    fun setStartedAt(value: Long?) {
        mutableUiState.update { it.copy(startedAt = value?.millisToLocalDate()) }
    }

    fun setCompletedAt(value: Long?) {
        mutableUiState.update { it.copy(completedAt = value?.millisToLocalDate()) }
    }

    fun onDateDialogOpen(dateType: Int) {
        mutableUiState.update {
            it.copy(
                selectedDateType = dateType,
                openDatePicker = true
            )
        }
    }

    fun onDateDialogClosed() {
        mutableUiState.update { it.copy(openDatePicker = false) }
    }

    fun onChangeRepeatCount(value: Int?): Boolean {
        if (value != null && value >= 0) {
            mutableUiState.update { it.copy(repeatCount = value) }
            return true
        }
        return false
    }

    fun setIsPrivate(value: Boolean) {
        mutableUiState.update { it.copy(isPrivate = value) }
    }

    fun setNotes(value: String) {
        mutableUiState.update { it.copy(notes = value) }
    }

    fun updateListEntry() = mediaListRepository.updateEntry(
        oldEntry = uiState.value.listEntry,
        mediaId = uiState.value.mediaDetails!!.id,
        status = uiState.value.status,
        score = uiState.value.score,
        progress = uiState.value.progress,
        progressVolumes = uiState.value.volumeProgress,
        startedAt = uiState.value.startedAt?.toFuzzyDate(),
        completedAt = uiState.value.completedAt?.toFuzzyDate(),
        repeat = uiState.value.repeatCount,
        private = uiState.value.isPrivate,
        notes = uiState.value.notes,
    ).onEach { result ->
        mutableUiState.update {
            if (result is DataResult.Success) {
                it.copy(
                    isLoading = false,
                    listEntry = result.data?.basicMediaListEntry ?: it.listEntry,
                    updateSuccess = result.data != null
                )
            } else {
                result.toUiState()
            }
        }
    }.catch {
        mutableUiState.update {
            it.copy(
                isLoading = false,
                updateSuccess = false
            )
        }
    }.launchIn(viewModelScope)

    @Suppress("UNCHECKED_CAST")
    fun updateCustomLists(customsList: List<String>) = mediaListRepository
        .updateEntry(
            mediaId = uiState.value.mediaDetails!!.id,
            customLists = customsList
        ).onEach { result ->
            mutableUiState.update {
                if (result is DataResult.Success) {
                    it.copy(
                        isLoading = false,
                        customLists = result.data?.customLists as? LinkedHashMap<String, Boolean>,
                        openCustomListsDialog = false
                    )
                } else {
                    result.toUiState()
                }
            }
        }.catch {
            mutableUiState.update { it.copy(isLoading = false) }
        }.launchIn(viewModelScope)

    fun getCustomLists() {
        uiState.value.listEntry?.id?.let { entryId ->
            userId
                .filterNotNull()
                .flatMapLatest { userId ->
                    mediaListRepository.getMediaListCustomLists(entryId, userId)
                }
                .onEach { result ->
                    mutableUiState.update {
                        if (result is DataResult.Success) {
                            it.copy(
                                isLoading = false,
                                customLists = result.data,
                                openCustomListsDialog = true
                            )
                        } else {
                            result.toUiState()
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun toggleCustomListsDialog(open: Boolean) {
        mutableUiState.update {
            it.copy(openCustomListsDialog = open)
        }
    }

    fun toggleDeleteDialog(open: Boolean) {
        mutableUiState.update {
            it.copy(openDeleteDialog = open)
        }
    }

    fun deleteListEntry() {
        uiState.value.listEntry?.id?.let { entryId ->
            mediaListRepository.deleteEntry(entryId)
                .onEach { result ->
                    mutableUiState.update {
                        if (result is DataResult.Success) {
                            it.copy(
                                isLoading = false,
                                listEntry = if (result.data != null) null else it.listEntry,
                                updateSuccess = result.data != null
                            )
                        } else {
                            result.toUiState()
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun setUpdateSuccess(value: Boolean) {
        mutableUiState.update { it.copy(updateSuccess = value) }
    }
}