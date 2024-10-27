package com.axiel7.anihyou.ui.screens.mediadetails.activity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.common.indexOfFirstOrNull
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.api.response.PagedResult
import com.axiel7.anihyou.data.model.activity.updateLikeStatus
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MediaActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository,
    private val likeRepository: LikeRepository,
    private val activityRepository: ActivityRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : PagedUiStateViewModel<MediaActivityUiState>(), MediaActivityEvent {

    private val arguments = savedStateHandle.toRoute<MediaActivity>()

    override val initialState = MediaActivityUiState()

    override fun setIsMine(value: Boolean) {
        mutableUiState.update {
            it.copy(isMine = value, page = 1, hasNextPage = true, isLoading = true)
        }
    }

    override fun toggleLikeActivity(id: Int) {
        val foundItem = mutableUiState.value.activities.find { it.id == id } ?: return
        val type = foundItem.type ?: return
        viewModelScope.launch {
            likeRepository.toggleActivityLike(
                id = id,
                type = type
            ).collect { result ->
                if (result is DataResult.Success) {
                    mutableUiState.value.run {
                        val foundIndex = activities.indexOf(foundItem)
                        if (foundIndex != -1) {
                            val oldItem = activities[foundIndex]
                            activities[foundIndex] = oldItem.updateLikeStatus(result.data)
                        }
                    }
                }
            }
        }
    }

    override fun deleteActivity(id: Int) {
        viewModelScope.launch {
            activityRepository.deleteActivity(id).collectLatest { result ->
                if (result is DataResult.Success && result.data == true) {
                    mutableUiState.value.run {
                        activities.indexOfFirstOrNull { it.id == id }?.let {
                            activities.removeAt(it)
                        }
                    }
                }
            }
        }
    }

    init {
        mutableUiState
            .filter { it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.isMine == new.isMine
            }
            .flatMapLatest { uiState ->
                mediaRepository.getMediaActivityPage(
                    mediaId = arguments.mediaId,
                    userId = defaultPreferencesRepository.userId.first().takeIf { uiState.isMine },
                    page = uiState.page,
                )
            }
            .onEach { result ->
                if (result is PagedResult.Success) {
                    mutableUiState.update {
                        if (it.page == 1) it.activities.clear()
                        it.activities.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                        )
                    }
                } else {
                    mutableUiState.update {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}