package com.axiel7.anihyou.wear.ui.screens.usermedialist.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.MediaListRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.isUsingVolumeProgress
import com.axiel7.anihyou.wear.ui.navigation.Routes
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditMediaViewModel(
    private val mediaRepository: MediaRepository,
    private val mediaListRepository: MediaListRepository,
    savedStateHandle: SavedStateHandle,
) : UiStateViewModel<EditMediaUiState>(), EditMediaEvent {

    private val mediaId = savedStateHandle.get<Int>(Routes.Arguments.ID)

    override val initialState = EditMediaUiState()

    override fun onClickPlusOne() {
        viewModelScope.launch {
            uiState.value.entry?.let { entry ->
                mediaListRepository.incrementProgress(
                    entry = entry.basicMediaListEntry,
                    total = entry.duration()
                ).collectLatest { result ->
                    mutableUiState.update { uiState ->
                        if (result is DataResult.Success) {
                            uiState.copy(
                                entry = uiState.entry?.copy(
                                    basicMediaListEntry = result.data?.basicMediaListEntry
                                        ?: uiState.entry.basicMediaListEntry
                                )
                            )
                        } else {
                            result.toUiState()
                        }
                    }
                }
            }
        }
    }

    override fun onClickMinusOne() {
        viewModelScope.launch {
            uiState.value.entry?.let { entry ->
                val isVolumeProgress = entry.basicMediaListEntry.isUsingVolumeProgress()
                mediaListRepository.updateEntry(
                    oldEntry = entry.basicMediaListEntry,
                    mediaId = entry.mediaId,
                    progress = (entry.basicMediaListEntry.progress?.minus(1))?.takeIf { !isVolumeProgress },
                    progressVolumes = (entry.basicMediaListEntry.progressVolumes?.minus(1))?.takeIf { isVolumeProgress },
                ).collectLatest { result ->
                    mutableUiState.update { uiState ->
                        if (result is DataResult.Success) {
                            uiState.copy(
                                entry = uiState.entry?.copy(
                                    basicMediaListEntry = result.data?.basicMediaListEntry
                                        ?: uiState.entry.basicMediaListEntry
                                )
                            )
                        } else {
                            result.toUiState()
                        }
                    }
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            mediaId?.let { mediaId ->
                mediaRepository.getBasicMediaDetails(mediaId)
                    .collectLatest { result ->
                        mutableUiState.update { uiState ->
                            if (result is DataResult.Success && result.data != null) {
                                uiState.copy(
                                    entry = result.data
                                )
                            } else {
                                result.toUiState()
                            }
                        }
                    }
            }
        }
    }
}