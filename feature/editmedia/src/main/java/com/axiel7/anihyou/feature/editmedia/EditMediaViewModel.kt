package com.axiel7.anihyou.feature.editmedia

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.utils.DateUtils.millisToLocalDate
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaListRepository
import com.axiel7.anihyou.core.model.media.advancedScoreNames
import com.axiel7.anihyou.core.model.media.advancedScoresMap
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.network.api.model.toFuzzyDate
import com.axiel7.anihyou.core.network.api.model.toLocalDate
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class EditMediaViewModel(
    private val mediaListRepository: MediaListRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<EditMediaUiState>(), EditMediaEvent {

    override val initialState = EditMediaUiState()

    private val userId = defaultPreferencesRepository.userId

    fun setMediaDetails(value: BasicMediaDetails) =
        mutableUiState.update { it.copy(mediaDetails = value) }

    fun setListEntry(value: BasicMediaListEntry?) = mutableUiState.update {
        value?.advancedScoresMap()?.let { advancedScores ->
            it.advancedScores.putAll(advancedScores)
        }
        it.copy(
            listEntry = value,
            status = value?.status,
            progress = value?.progress,
            volumeProgress = value?.progressVolumes,
            score = value?.score,
            advancedScoresNames = value?.advancedScoreNames()?.toList().orEmpty(),
            startedAt = value?.startedAt?.fuzzyDate?.toLocalDate(),
            completedAt = value?.completedAt?.fuzzyDate?.toLocalDate(),
            repeatCount = value?.repeat,
            isPrivate = value?.private,
            isHiddenFromStatusLists = value?.hiddenFromStatusLists,
            notes = value?.notes,
        )
    }

    fun fillCustomLists(mediaType: MediaType?) {
        viewModelScope.launch {
            val customLists = linkedMapOf<String, Boolean>()
            val savedCustomsLists = if (mediaType == MediaType.ANIME) {
                defaultPreferencesRepository.animeCustomLists.first()
            } else {
                defaultPreferencesRepository.mangaCustomLists.first()
            }
            savedCustomsLists?.forEach { customLists[it] = false }
            mutableUiState.update { it.copy(customLists = customLists) }
        }
    }

    override fun onChangeStatus(value: MediaListStatus) {
        val prevStatus = uiState.value.status
        val hasStarted = (uiState.value.isNewEntry || prevStatus == MediaListStatus.PLANNING)
                && value == MediaListStatus.CURRENT
        val hasCompleted = value == MediaListStatus.COMPLETED
        if (hasStarted) {
            mutableUiState.update {
                it.copy(
                    status = value,
                    startedAt = LocalDate.now()
                )
            }
        } else if (hasCompleted) {
            mutableUiState.update { uiState ->
                uiState.copy(
                    status = value,
                    progress = uiState.mediaHasDuration() ?: uiState.progress,
                    volumeProgress = uiState.mediaHasVolumes() ?: uiState.volumeProgress,
                    startedAt = uiState.startedAt ?: LocalDate.now(),
                    completedAt = uiState.completedAt ?: LocalDate.now()
                )
            }
        } else {
            mutableUiState.update { it.copy(status = value) }
        }
    }

    override fun onChangeProgress(value: Int?) {
        val totalDuration = uiState.value.mediaDetails?.duration()
        if (canChangeProgressTo(value, totalDuration)) {
            mutableUiState.update {
                if (it.status == null || it.status == MediaListStatus.PLANNING
                    || it.status == MediaListStatus.PAUSED
                ) {
                    onChangeStatus(MediaListStatus.CURRENT)
                } else if (totalDuration != null && value != null && value >= totalDuration) {
                    onChangeStatus(MediaListStatus.COMPLETED)
                }
                it.copy(progress = value)
            }
        }
    }

    override fun onChangeVolumeProgress(value: Int?) {
        val totalVolumes = uiState.value.mediaDetails?.volumes
        if (canChangeProgressTo(value, totalVolumes)) {
            mutableUiState.update {
                if (it.status == null || it.status == MediaListStatus.PLANNING) {
                    onChangeStatus(MediaListStatus.CURRENT)
                } else if (totalVolumes != null && value != null && value >= totalVolumes) {
                    onChangeStatus(MediaListStatus.COMPLETED)
                }
                it.copy(volumeProgress = value)
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

    override fun onChangeScore(value: Double?) {
        mutableUiState.update { it.copy(score = value) }
    }

    override fun setAdvancedScore(key: String, value: Double?) {
        mutableUiState.update { uiState ->
            uiState.advancedScores[key] = value ?: 0.0
            val scoresToSum = uiState.advancedScores.values.filter { it > 0.0 }
            uiState.copy(
                score = scoresToSum.sum() / scoresToSum.size
            )
        }
    }

    override fun setStartedAt(value: Long?) {
        mutableUiState.update { it.copy(startedAt = value?.millisToLocalDate()) }
    }

    override fun setCompletedAt(value: Long?) {
        mutableUiState.update { it.copy(completedAt = value?.millisToLocalDate()) }
    }

    override fun onDateDialogOpen(dateType: Int) {
        mutableUiState.update {
            it.copy(
                selectedDateType = dateType,
                openDatePicker = true
            )
        }
    }

    override fun onDateDialogClosed() {
        mutableUiState.update { it.copy(openDatePicker = false) }
    }

    override fun onChangeRepeatCount(value: Int?): Boolean {
        if (value != null && value >= 0) {
            mutableUiState.update { it.copy(repeatCount = value) }
            return true
        }
        return false
    }

    override fun setIsPrivate(value: Boolean) {
        mutableUiState.update { it.copy(isPrivate = value) }
    }

    override fun setIsHiddenFromStatusLists(value: Boolean) {
        mutableUiState.update { it.copy(isHiddenFromStatusLists = value) }
    }

    override fun setNotes(value: String) {
        mutableUiState.update { it.copy(notes = value) }
    }

    override fun updateListEntry() {
        mutableUiState.value.run {
            mediaListRepository.updateEntry(
                oldEntry = listEntry,
                mediaId = mediaDetails!!.id,
                status = status,
                score = score,
                advancedScores = advancedScoresNames.mapNotNull { advancedScores[it] }
                    .takeIf { advancedScores.isNotEmpty() },
                progress = progress,
                progressVolumes = volumeProgress,
                startedAt = startedAt?.toFuzzyDate(),
                completedAt = completedAt?.toFuzzyDate(),
                repeat = repeatCount,
                private = isPrivate,
                hiddenFromStatusLists = isHiddenFromStatusLists,
                notes = notes,
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
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun updateCustomLists(customsList: List<String>) {
        mediaListRepository
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
    }

    override fun getCustomLists() {
        viewModelScope.launch {
            val userId = userId.first() ?: return@launch
            val entryId = mutableUiState.value.listEntry?.id ?: return@launch
            mediaListRepository.getMediaListCustomLists(
                id = entryId,
                userId = userId
            ).collectLatest { result ->
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
        }
    }

    override fun toggleCustomListsDialog(open: Boolean) {
        mutableUiState.update {
            it.copy(openCustomListsDialog = open)
        }
    }

    override fun toggleDeleteDialog(open: Boolean) {
        mutableUiState.update {
            it.copy(openDeleteDialog = open)
        }
    }

    override fun deleteListEntry() {
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

    override fun setUpdateSuccess(value: Boolean) {
        mutableUiState.update { it.copy(updateSuccess = value) }
    }

    init {
        defaultPreferencesRepository.scoreFormat
            .filterNotNull()
            .onEach { value ->
                mutableUiState.update { it.copy(scoreFormat = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.advancedScoringEnabled
            .onEach { value ->
                mutableUiState.update { it.copy(advancedScoringEnabled = value) }
            }
            .launchIn(viewModelScope)
    }
}