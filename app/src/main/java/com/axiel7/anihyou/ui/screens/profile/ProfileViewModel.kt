package com.axiel7.anihyou.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.api.response.PagedResult
import com.axiel7.anihyou.data.model.activity.updateLikeStatus
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val likeRepository: LikeRepository,
    private val activityRepository: ActivityRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : PagedUiStateViewModel<ProfileUiState>(), ProfileEvent {

    private val arguments = runCatching { savedStateHandle.toRoute<UserDetails>() }.getOrNull()

    override val initialState = ProfileUiState(
        isMyProfile = arguments == null || arguments.id == 0 && arguments.userName == null,
    )

    private fun getMyUserInfo() {
        mutableUiState
            .filter { it.userInfo == null }
            .flatMapLatest {
                userRepository.getMyUserInfo()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success && result.data != null) {
                        // refresh user options
                        defaultPreferencesRepository.saveProfileInfo(result.data)
                        it.copy(
                            userInfo = result.data,
                            isLoading = false
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getUserInfo(
        userId: Int? = null,
        username: String? = null,
    ) {
        mutableUiState
            .filter { it.userInfo == null }
            .flatMapLatest { userRepository.getUserInfo(userId, username) }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            userInfo = result.data,
                            isLoading = false,
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)
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
                                textActivityFragment = oldItem.onTextActivity.textActivityFragment
                                    .updateLikeStatus(result.data.isLiked == true)
                            ),
                            onListActivity = oldItem.onListActivity?.copy(
                                listActivityFragment = oldItem.onListActivity.listActivityFragment
                                    .updateLikeStatus(result.data.isLiked == true)
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

    init {
        if (mutableUiState.value.isMyProfile) getMyUserInfo()
        else getUserInfo(arguments?.id, arguments?.userName)

        // activities
        mutableUiState
            .filter { it.userInfo != null && it.hasNextPage && it.page != 0 }
            .distinctUntilChangedBy { it.page }
            .flatMapLatest { uiState ->
                userRepository.getUserActivity(
                    userId = uiState.userInfo!!.id,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.activities.addAll(result.list)
                        it.copy(
                            isLoadingActivity = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        it.copy(
                            isLoadingActivity = result is PagedResult.Loading && it.page == 1
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}