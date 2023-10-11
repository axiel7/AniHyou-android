package com.axiel7.anihyou.ui.screens.profile

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.UserActivityQuery
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.activity.updateLikeStatus
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val userRepository: UserRepository,
    private val likeRepository: LikeRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : PagedUiStateViewModel<ProfileUiState>() {

    override val mutableUiState = MutableStateFlow(ProfileUiState())
    override val uiState = mutableUiState.asStateFlow()

    fun getMyUserInfo() {
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

    fun getUserInfo(
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

    fun toggleFollow() {
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

    val userActivities = mutableStateListOf<UserActivityQuery.Activity>()

    fun toggleLikeActivity(id: Int) {
        likeRepository.toggleLike(
            likeableId = id,
            type = LikeableType.ACTIVITY
        ).onEach { result ->
            if (result is DataResult.Success && result.data != null) {
                val foundIndex = userActivities.indexOfFirst {
                    it.onListActivity?.listActivityFragment?.id == id
                            || it.onTextActivity?.textActivityFragment?.id == id
                }
                if (foundIndex != -1) {
                    val oldItem = userActivities[foundIndex]
                    userActivities[foundIndex] = oldItem.copy(
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

    val accessToken = defaultPreferencesRepository.accessToken.stateInViewModel()

    init {
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
                        //if (it.page == 1) userActivities.clear()
                        userActivities.addAll(result.list)
                        it.copy(
                            isLoadingActivity = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}