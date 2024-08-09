package com.axiel7.anihyou.ui.screens.profile.social

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.api.response.PagedResult
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserSocialViewModel @Inject constructor(
    private val userRepository: UserRepository
) : PagedUiStateViewModel<UserSocialUiState>(), UserSocialEvent {

    override val initialState = UserSocialUiState()

    fun setUserId(value: Int) = mutableUiState.update { it.copy(userId = value) }

    override fun setType(value: UserSocialType) {
        mutableUiState.update {
            it.copy(type = value, page = 1, hasNextPage = true)
        }
    }

    init {
        // followers
        mutableUiState
            .filter {
                it.type == UserSocialType.FOLLOWERS
                        && it.hasNextPage
                        && it.userId != null
            }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    userRepository.getFollowers(
                        userId = uiState.userId,
                        page = uiState.page
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.followers.clear()
                        it.followers.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        // following
        mutableUiState
            .filter {
                it.type == UserSocialType.FOLLOWING
                        && it.hasNextPage
                        && it.userId != null
            }
            .flatMapLatest { uiState ->
                if (uiState.userId != null)
                    userRepository.getFollowing(
                        userId = uiState.userId,
                        page = uiState.page
                    )
                else emptyFlow()
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.following.clear()
                        it.following.addAll(result.list)
                        it.copy(
                            isLoading = false,
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