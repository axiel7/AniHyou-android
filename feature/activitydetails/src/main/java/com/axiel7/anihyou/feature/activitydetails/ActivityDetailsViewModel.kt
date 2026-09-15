package com.axiel7.anihyou.feature.activitydetails

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.ActivityRepository
import com.axiel7.anihyou.core.domain.repository.LikeRepository
import com.axiel7.anihyou.core.model.activity.toGenericActivity
import com.axiel7.anihyou.core.model.activity.updateLikeStatus
import com.axiel7.anihyou.core.network.ActivityDetailsQuery
import com.axiel7.anihyou.core.ui.common.navigation.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityDetailsViewModel(
    @InjectedParam arguments: Route.ActivityDetails,
    private val activityRepository: ActivityRepository,
    private val likeRepository: LikeRepository,
) : UiStateViewModel<ActivityDetailsUiState>(), ActivityDetailsEvent {

    private var detailsQueryData: ActivityDetailsQuery.Activity? = null

    override val initialState = ActivityDetailsUiState()

    override fun toggleLikeActivity() {
        val details = mutableUiState.value.details ?: return
        val queryData = detailsQueryData ?: return
        viewModelScope.launch {
            val result = likeRepository.toggleActivityLike(details.id, details.type)
            if (result is DataResult.Success) {
                val newData = queryData.copy(
                    onTextActivity = queryData.onTextActivity?.copy(
                        textActivityFragment = queryData.onTextActivity!!.textActivityFragment
                            .updateLikeStatus(result.data)
                    ),
                    onListActivity = queryData.onListActivity?.copy(
                        listActivityFragment = queryData.onListActivity!!.listActivityFragment
                            .updateLikeStatus(result.data)
                    ),
                    onMessageActivity = queryData.onMessageActivity?.copy(
                        messageActivityFragment = queryData.onMessageActivity!!.messageActivityFragment
                            .updateLikeStatus(result.data)
                    )
                )
                activityRepository.updateActivityCache(
                    listActivity = newData.onListActivity?.listActivityFragment,
                    textActivity = newData.onTextActivity?.textActivityFragment,
                    messageActivity = newData.onMessageActivity?.messageActivityFragment,
                )
                detailsQueryData = newData
                mutableUiState.update {
                    it.copy(
                        details = newData.onTextActivity?.toGenericActivity()
                            ?: newData.onListActivity?.toGenericActivity()
                            ?: newData.onMessageActivity?.toGenericActivity()
                    )
                }
            } else {
                mutableUiState.update { it.copy(error = "Like failed") }
            }
        }
    }

    override fun toggleLikeReply(id: Int) {
        viewModelScope.launch {
            likeRepository.toggleActivityReplyLike(id).let { result ->
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