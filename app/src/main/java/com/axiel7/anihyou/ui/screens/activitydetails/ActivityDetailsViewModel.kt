package com.axiel7.anihyou.ui.screens.activitydetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.ActivityDetailsQuery
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.activity.toGenericActivity
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.type.ActivityType
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivityDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val activityRepository: ActivityRepository,
    private val likeRepository: LikeRepository,
) : UiStateViewModel<ActivityDetailsUiState>(), ActivityDetailsEvent {

    val arguments = savedStateHandle.toRoute<ActivityDetails>()

    private var detailsQueryData: ActivityDetailsQuery.Activity? = null

    override val initialState = ActivityDetailsUiState()

    override fun toggleLikeActivity() {
        val details = mutableUiState.value.details ?: return
        val queryData = detailsQueryData ?: return
        viewModelScope.launch {
            when (details.type) {
                ActivityType.TEXT -> {
                    likeRepository.toggleTextActivityLike(details.id).collect { result ->
                        if (result is DataResult.Success && result.data != null) {
                            activityRepository.updateActivityDetailsCache(
                                id = details.id,
                                activity = queryData.copy(
                                    onTextActivity = queryData.onTextActivity?.copy(
                                        textActivityFragment = result.data
                                    )
                                )
                            )
                        } else if (result !is DataResult.Loading) {
                            mutableUiState.update {
                                it.copy(error = "Like failed")
                            }
                        }
                    }
                }

                ActivityType.MESSAGE -> {
                    likeRepository.toggleMessageActivityLike(details.id).collect { result ->
                        if (result is DataResult.Success && result.data != null) {
                            activityRepository.updateActivityDetailsCache(
                                id = details.id,
                                activity = queryData.copy(
                                    onMessageActivity = queryData.onMessageActivity?.copy(
                                        messageActivityFragment = result.data
                                    )
                                )
                            )
                        } else if (result !is DataResult.Loading) {
                            mutableUiState.update {
                                it.copy(error = "Like failed")
                            }
                        }
                    }
                }

                else -> {
                    likeRepository.toggleListActivityLike(details.id).collect { result ->
                        if (result is DataResult.Success && result.data != null) {
                            activityRepository.updateActivityDetailsCache(
                                id = details.id,
                                activity = queryData.copy(
                                    onListActivity = queryData.onListActivity?.copy(
                                        listActivityFragment = result.data.copy(
                                            // for some reason the API returns null media
                                            media = queryData.onListActivity.listActivityFragment.media
                                        )
                                    )
                                )
                            )
                        } else if (result !is DataResult.Loading) {
                            mutableUiState.update {
                                it.copy(error = "Like failed")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun toggleLikeReply(id: Int) {
        viewModelScope.launch {
            likeRepository.toggleActivityReplyLike(id).collect { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.value.run {
                        val foundIndex = replies.indexOfFirst { it.id == id }
                        if (foundIndex != -1) {
                            replies[foundIndex] = result.data
                        }
                    }
                } else if (result !is DataResult.Loading) {
                    mutableUiState.update {
                        it.copy(error = "Like failed")
                    }
                }
            }
        }
    }

    override fun refresh() {
        mutableUiState.update { it.copy(fetchFromNetwork = true) }
    }

    init {
        mutableUiState
            .distinctUntilChanged { _, new -> !new.fetchFromNetwork }
            .flatMapLatest { uiState ->
                activityRepository.getActivityDetails(
                    activityId = arguments.id,
                    fetchFromNetwork = uiState.fetchFromNetwork
                )
            }
            .onEach { result ->
                if (result is DataResult.Success) {
                    detailsQueryData = result.data
                    mutableUiState.updateAndGet {
                        it.copy(
                            isLoading = false,
                            fetchFromNetwork = false,
                            details = result.data?.onTextActivity?.toGenericActivity()
                                ?: result.data?.onListActivity?.toGenericActivity()
                                ?: result.data?.onMessageActivity?.toGenericActivity()
                        )
                    }.also {
                        if (it.replies.isEmpty()) it.replies.addAll(it.details?.replies.orEmpty())
                    }
                } else {
                    mutableUiState.update { result.toUiState() }
                }
            }
            .launchIn(viewModelScope)
    }
}