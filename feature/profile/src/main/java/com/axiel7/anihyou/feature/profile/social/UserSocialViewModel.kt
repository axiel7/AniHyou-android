package com.axiel7.anihyou.feature.profile.social

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.InjectedParam

@OptIn(ExperimentalCoroutinesApi::class)
class UserSocialViewModel(
    @InjectedParam userId: Int,
    private val userRepository: UserRepository
) : PagedUiStateViewModel<UserSocialUiState>(), UserSocialEvent {

    override val initialState = UserSocialUiState(userId = userId)

    override fun setType(value: UserSocialType) {
        mutableUiState.update {
            it.copy(type = value, page = 1, hasNextPage = true)
        }
    }

    override fun onRefresh() {
        mutableUiState.update { it.copy(page = 1, hasNextPage = true, fetchFromNetwork = true) }
    }

    init {
        // followers
        mutableUiState
            .filter {
                it.type == UserSocialType.FOLLOWERS
                        && it.hasNextPage
            }
            .distinctUntilChanged { _, new -> !new.fetchFromNetwork }
            .flatMapLatest { uiState ->
                userRepository.getFollowers(
                    userId = uiState.userId,
                    page = uiState.page,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.followers.clear()
                        it.followers.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
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
            }
            .distinctUntilChanged { _, new -> !new.fetchFromNetwork }
            .flatMapLatest { uiState ->
                userRepository.getFollowing(
                    userId = uiState.userId,
                    page = uiState.page,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.following.clear()
                        it.following.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}