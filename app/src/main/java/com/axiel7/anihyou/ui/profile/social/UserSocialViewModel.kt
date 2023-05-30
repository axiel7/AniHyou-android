package com.axiel7.anihyou.ui.profile.social

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.FollowersQuery
import com.axiel7.anihyou.FollowingsQuery
import com.axiel7.anihyou.ui.base.BaseViewModel

class UserSocialViewModel : BaseViewModel() {

    var userSocialType by mutableStateOf(UserSocialType.FOLLOWERS)

    suspend fun onUserSocialTypeChanged(userId: Int) {
        when (userSocialType) {
            UserSocialType.FOLLOWERS -> if (hasNextPageFollowers) getFollowers(userId)
            UserSocialType.FOLLOWING -> if (hasNextPageFollowing) getFollowing(userId)
        }
    }

    private var pageFollowers = 1
    var hasNextPageFollowers = true
    var followers = mutableStateListOf<FollowersQuery.Follower>()

    suspend fun getFollowers(userId: Int) {
        val response = FollowersQuery(
            userId = userId,
            page = Optional.present(pageFollowers),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.Page?.followers?.filterNotNull()?.let { followers.addAll(it) }
        hasNextPageFollowers = response?.data?.Page?.pageInfo?.hasNextPage ?: false
        pageFollowers = response?.data?.Page?.pageInfo?.currentPage?.plus(1) ?: pageFollowers
    }

    private var pageFollowing = 1
    var hasNextPageFollowing = true
    var following = mutableStateListOf<FollowingsQuery.Following>()

    suspend fun getFollowing(userId: Int) {
        val response = FollowingsQuery(
            userId = userId,
            page = Optional.present(pageFollowing),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.Page?.following?.filterNotNull()?.let { following.addAll(it) }
        hasNextPageFollowing = response?.data?.Page?.pageInfo?.hasNextPage ?: false
        pageFollowing = response?.data?.Page?.pageInfo?.currentPage?.plus(1) ?: pageFollowing
    }
}