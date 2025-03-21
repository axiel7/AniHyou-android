package com.axiel7.anihyou.feature.profile.social

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.common.PagedResult
import com.axiel7.anihyou.core.domain.repository.UserRepository
import com.axiel7.anihyou.core.ui.common.viewmodel.PagedUiStateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class UserSocialViewModel(
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