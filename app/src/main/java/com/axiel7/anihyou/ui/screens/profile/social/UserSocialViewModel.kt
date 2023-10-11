package com.axiel7.anihyou.ui.screens.profile.social

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.FollowersQuery
import com.axiel7.anihyou.FollowingsQuery
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.ui.screens.profile.USER_ID_ARGUMENT
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSocialViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository
) : ViewModel() {

    //TODO: user social

    val userId: Int = savedStateHandle[USER_ID_ARGUMENT.removeFirstAndLast()]!!

    var isLoading by mutableStateOf(true)
        private set

    var userSocialType by mutableStateOf(UserSocialType.FOLLOWERS)
        private set

    fun onUserSocialTypeChanged(value: UserSocialType) {
        userSocialType = value
        when (userSocialType) {
            UserSocialType.FOLLOWERS -> if (hasNextPageFollowers) getFollowers()
            UserSocialType.FOLLOWING -> if (hasNextPageFollowing) getFollowing()
        }
    }

    private var pageFollowers = 1
    private var hasNextPageFollowers = true
    var followers = mutableStateListOf<FollowersQuery.Follower>()

    private fun getFollowers() = viewModelScope.launch {
        userRepository.getFollowers(
            userId = userId,
            page = pageFollowers
        ).collect { result ->
            isLoading = pageFollowers == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                followers.addAll(result.list)
                //hasNextPageFollowers = result.nextPage != null
                //pageFollowers = result.nextPage ?: pageFollowers
            } else if (result is PagedResult.Error) {
                //message = result.message
            }
        }
    }

    private var pageFollowing = 1
    private var hasNextPageFollowing = true
    var following = mutableStateListOf<FollowingsQuery.Following>()

    private fun getFollowing() = viewModelScope.launch {
        userRepository.getFollowing(
            userId = userId,
            page = pageFollowing
        ).collect { result ->
            isLoading = pageFollowing == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                following.addAll(result.list)
                //hasNextPageFollowing = result.nextPage != null
                //pageFollowing = result.nextPage ?: pageFollowing
            } else if (result is PagedResult.Error) {
                //message = result.message
            }
        }
    }
}