package com.axiel7.anihyou.feature.home.activity

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.domain.repository.ActivityRepository
import com.axiel7.anihyou.core.domain.repository.LikeRepository
import com.axiel7.anihyou.core.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.core.model.activity.updateLikeStatus
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityFeedViewModel(
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
        val foundItem = mutableUiState.value.activities.find {
            it.onListActivity?.listActivityFragment?.id == id
                    || it.onTextActivity?.textActivityFragment?.id == id
        } ?: return
        val type = when {
            foundItem.onTextActivity != null -> ActivityType.TEXT
            foundItem.onMessageActivity != null -> ActivityType.MESSAGE
            else -> ActivityType.MEDIA_LIST
        }
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
                            activities[foundIndex] = oldItem.copy(
                                onTextActivity = oldItem.onTextActivity?.copy(
                                    textActivityFragment = oldItem.onTextActivity!!.textActivityFragment
                                        .updateLikeStatus(result.data)
                                ),
                                onListActivity = oldItem.onListActivity?.copy(
                                    listActivityFragment = oldItem.onListActivity!!.listActivityFragment
                                        .updateLikeStatus(result.data)
                                ),
                                onMessageActivity = oldItem.onMessageActivity?.copy(
                                    messageActivityFragment = oldItem.onMessageActivity!!.messageActivityFragment
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
                    typeIn = it.type.value,
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
                        result.toUiState(loadingWhen = it.page == 1).copy(hasNextPage = false)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}