package com.axiel7.anihyou.ui.screens.home.activity

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.data.model.activity.updateLikeStatus
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivityFeedViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val likeRepository: LikeRepository,
) : PagedUiStateViewModel<ActivityFeedUiState>(), ActivityFeedEvent {

    override val initialState = ActivityFeedUiState()

    override fun setIsFollowing(value: Boolean) {
        mutableUiState.update {
            it.copy(isFollowing = value, page = 1, hasNextPage = true)
        }
    }

    override fun setType(value: ActivityTypeGrouped) {
        mutableUiState.update {
            it.copy(type = value, page = 1, hasNextPage = true)
        }
    }

    override fun refreshList() {
        mutableUiState.update {
            it.copy(fetchFromNetwork = true, page = 1, hasNextPage = true, isLoading = true)
        }
    }

    override fun toggleLikeActivity(id: Int) {
        viewModelScope.launch {
            likeRepository.toggleLike(
                likeableId = id,
                type = LikeableType.ACTIVITY
            ).collect { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.value.run {
                        val foundIndex = activities.indexOfFirst {
                            it.onListActivity?.listActivityFragment?.id == id
                                    || it.onTextActivity?.textActivityFragment?.id == id
                        }
                        if (foundIndex != -1) {
                            val oldItem = activities[foundIndex]
                            activities[foundIndex] = oldItem.copy(
                                onTextActivity = oldItem.onTextActivity?.copy(
                                    textActivityFragment = oldItem.onTextActivity.textActivityFragment
                                        .updateLikeStatus(result.data)
                                ),
                                onListActivity = oldItem.onListActivity?.copy(
                                    listActivityFragment = oldItem.onListActivity.listActivityFragment
                                        .updateLikeStatus(result.data)
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    init {
        //first load
        refreshList()

        mutableUiState
            .filter { it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.isFollowing == new.isFollowing
                        && old.type == new.type
                        && !new.fetchFromNetwork
            }
            .flatMapLatest {
                activityRepository.getActivityFeed(
                    isFollowing = it.isFollowing,
                    type = it.type,
                    fetchFromNetwork = it.fetchFromNetwork,
                    page = it.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.activities.clear()
                        it.activities.addAll(result.list)
                        it.copy(
                            fetchFromNetwork = false,
                            hasNextPage = result.hasNextPage,
                            isLoading = false
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}