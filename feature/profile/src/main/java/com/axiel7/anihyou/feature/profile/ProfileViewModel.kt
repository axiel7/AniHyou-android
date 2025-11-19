package com.axiel7.anihyou.feature.profile

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.domain.repository.ActivityRepository
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.LikeRepository
import com.axiel7.anihyou.core.domain.repository.UserRepository
import com.axiel7.anihyou.core.model.activity.updateLikeStatus
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel(
    private val arguments: Routes.UserDetails,
    private val userRepository: UserRepository,
    private val likeRepository: LikeRepository,
    private val activityRepository: ActivityRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : PagedUiStateViewModel<ProfileUiState>(), ProfileEvent {

    private val myUserId = defaultPreferencesRepository.userId

    override val initialState = ProfileUiState(
        isMyProfile = (arguments.id == null && arguments.userName == null),
    )

    private fun getMyUserInfo() {
        viewModelScope.launch {
            userRepository.getMyUserInfo(fetchFromNetwork = uiState.value.fetchFromNetwork)
                .collectLatest { result ->
                    mutableUiState.update {
                        if (result is DataResult.Success && result.data != null) {
                            // refresh user options
                            defaultPreferencesRepository.saveProfileInfo(result.data!!)
                            it.copy(
                                userInfo = result.data,
                                fetchFromNetwork = false,
                                isLoading = false
                            )
                        } else {
                            result.toUiState()
                        }
                    }
                }
        }
    }

    private fun getUserInfo(
        userId: Int? = null,
        username: String? = null,
    ) {
        viewModelScope.launch {
            userRepository.getUserInfo(
                userId = userId,
                username = username,
                fetchFromNetwork = uiState.value.fetchFromNetwork
            ).collectLatest { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            userInfo = result.data,
                            isMyProfile = result.data?.id == myUserId.first(),
                            fetchFromNetwork = false,
                            isLoading = false,
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
        }
    }

    override fun toggleFollow() {
        uiState.value.userInfo?.id?.let { userId ->
            userRepository.toggleFollow(userId)
                .onEach { result ->
                    mutableUiState.update {
                        it.copy(
                            userInfo = it.userInfo?.copy(
                                isFollowing = (result as? DataResult.Success)?.data?.isFollowing
                            ),
                            error = (result as? DataResult.Error)?.message
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    override fun toggleLikeActivity(id: Int) {
        likeRepository.toggleListActivityLike(id).onEach { result ->
            if (result is DataResult.Success && result.data != null) {
                mutableUiState.value.run {
                    findActivityIndex(id)?.let { foundIndex ->
                        val oldItem = activities[foundIndex]
                        activities[foundIndex] = oldItem.copy(
                            onTextActivity = oldItem.onTextActivity?.copy(
                                textActivityFragment = oldItem.onTextActivity!!.textActivityFragment
                                    .updateLikeStatus(result.data?.isLiked == true)
                            ),
                            onListActivity = oldItem.onListActivity?.copy(
                                listActivityFragment = oldItem.onListActivity!!.listActivityFragment
                                    .updateLikeStatus(result.data?.isLiked == true)
                            )
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun deleteActivity(id: Int) {
        activityRepository.deleteActivity(id).onEach { result ->
            if (result is DataResult.Success && result.data == true) {
                mutableUiState.value.run {
                    findActivityIndex(id)?.let { foundIndex ->
                        activities.removeAt(foundIndex)
                    }
                }
            } else if (result is DataResult.Error) {
                mutableUiState.update { it.copy(error = result.message) }
            }
        }.launchIn(viewModelScope)
    }

    override fun onRefresh() {
        mutableUiState.update { it.copy(fetchFromNetwork = true, isLoading = true) }
        if (mutableUiState.value.isMyProfile) getMyUserInfo()
        else getUserInfo(arguments.id, arguments.userName)
    }

    override fun onRefreshActivities() {
        mutableUiState.update {
            it.copy(
                isLoadingActivity = true,
                fetchFromNetwork = true,
                page = 1,
                hasNextPage = true
            )
        }
    }

    init {
        if (mutableUiState.value.isMyProfile) getMyUserInfo()
        else getUserInfo(arguments.id, arguments.userName)

        // activities
        mutableUiState
            .filter { it.userInfo != null && it.hasNextPage && it.page != 0 }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                userRepository.getUserActivity(
                    userId = uiState.userInfo!!.id,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.activities.clear()
                        it.activities.addAll(result.list)
                        it.copy(
                            isLoadingActivity = false,
                            fetchFromNetwork = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        it.copy(
                            isLoadingActivity = result is PagedResult.Loading && it.page == 1,
                            error = (result as? PagedResult.Error)?.message
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}