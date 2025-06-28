package com.axiel7.anihyou.feature.activitydetails

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.domain.repository.ActivityRepository
import com.axiel7.anihyou.core.domain.repository.LikeRepository
import com.axiel7.anihyou.core.model.activity.toGenericActivity
import com.axiel7.anihyou.core.network.ActivityDetailsQuery
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityDetailsViewModel(
    arguments: Routes.ActivityDetails,
    private val activityRepository: ActivityRepository,
    private val likeRepository: LikeRepository,
) : UiStateViewModel<ActivityDetailsUiState>(), ActivityDetailsEvent {

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
                                        textActivityFragment = result.data!!
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
                                        messageActivityFragment = result.data!!
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
                                        listActivityFragment = result.data!!.copy(
                                            // for some reason the API returns null media
                                            media = queryData.onListActivity!!.listActivityFragment.media
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
                            replies[foundIndex] = result.data!!.toGenericActivity()
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
                    mutableUiState.updateAndGet { uiState ->
                        uiState.copy(
                            isLoading = false,
                            fetchFromNetwork = false,
                            details = result.data?.onTextActivity?.toGenericActivity()
                                ?: result.data?.onListActivity?.toGenericActivity()
                                ?: result.data?.onMessageActivity?.toGenericActivity()
                        )
                    }.also { uiState ->
                        uiState.replies.clear()
                        uiState.replies.addAll(
                            uiState.details?.replies?.map { it.toGenericActivity() }.orEmpty()
                        )
                    }
                } else {
                    mutableUiState.update { result.toUiState() }
                }
            }
            .launchIn(viewModelScope)
    }
}