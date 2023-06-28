package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.App
import com.axiel7.anihyou.FollowersQuery
import com.axiel7.anihyou.FollowingsQuery
import com.axiel7.anihyou.ToggleFollowMutation
import com.axiel7.anihyou.UnreadNotificationCountQuery
import com.axiel7.anihyou.UpdateUserMutation
import com.axiel7.anihyou.UserActivityQuery
import com.axiel7.anihyou.UserBasicInfoQuery
import com.axiel7.anihyou.UserOptionsQuery
import com.axiel7.anihyou.UserStatsAnimeOverviewQuery
import com.axiel7.anihyou.UserStatsMangaOverviewQuery
import com.axiel7.anihyou.ViewerQuery
import com.axiel7.anihyou.data.PreferencesDataStore
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryMutation
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import com.axiel7.anihyou.type.ActivitySort
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

object UserRepository {

    fun getUnreadNotificationCount() = flow {
        val accessToken = App.dataStore.data.first()[PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY]
        if (accessToken != null) {
            val response = UnreadNotificationCountQuery().tryQuery()
            emit(response?.data?.Viewer?.unreadNotificationCount ?: 0)
        }
        else emit(0)
    }

    fun getUserOptions() = flow {
        emit(DataResult.Loading)

        val response = UserOptionsQuery().tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val options = response?.data?.Viewer?.options
            if (options != null) emit(DataResult.Success(data = options))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun updateUser(
        displayAdultContent: Boolean? = null
    ) = flow {
        emit(DataResult.Loading)
        val response = UpdateUserMutation(
            displayAdultContent = Optional.presentIfNotNull(displayAdultContent)
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val user = response?.data?.UpdateUser
            if (user != null) emit(DataResult.Success(data = user))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun getMyUserInfo() = flow {
        emit(DataResult.Loading)

        val response = ViewerQuery().tryQuery()
        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val userInfo = response?.data?.Viewer?.userInfo
            if (userInfo != null) emit(DataResult.Success(data = userInfo))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun getUserInfo(
        userId: Int? = null,
        username: String? = null,
    ) = flow {
        emit(DataResult.Loading)

        val response = UserBasicInfoQuery(
            userId = Optional.presentIfNotNull(userId),
            name = Optional.presentIfNotNull(username)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val userInfo = response?.data?.User?.userInfo
            if (userInfo != null) emit(DataResult.Success(data = userInfo))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun toggleFollow(userId: Int) = flow {
        emit(DataResult.Loading)

        val response = ToggleFollowMutation(
            userId = Optional.present(userId)
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val follow = response?.data?.ToggleFollow
            if (follow != null) emit(DataResult.Success(data = follow))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun getUserActivity(
        userId: Int,
        sort: List<ActivitySort> = listOf(ActivitySort.ID_DESC),
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = UserActivityQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            userId = Optional.present(userId),
            sort = Optional.present(sort)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val activities = response?.data?.Page?.activities?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (activities != null) emit(PagedResult.Success(
                data = activities,
                nextPage = if (pageInfo?.hasNextPage == true)
                    pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getOverviewAnimeStats(userId: Int) = flow {
        emit(DataResult.Loading)

        val response = UserStatsAnimeOverviewQuery(
            userId = Optional.present(userId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val stats = response?.data?.User?.statistics?.anime
            if (stats != null) emit(DataResult.Success(data = stats))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun getOverviewMangaStats(userId: Int) = flow {
        emit(DataResult.Loading)

        val response = UserStatsMangaOverviewQuery(
            userId = Optional.present(userId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val stats = response?.data?.User?.statistics?.manga
            if (stats != null) emit(DataResult.Success(data = stats))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun getFollowers(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = FollowersQuery(
            userId = userId,
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val followers = response?.data?.Page?.followers?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (followers != null) emit(PagedResult.Success(
                data = followers,
                nextPage = if (pageInfo?.hasNextPage == true)
                    pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getFollowing(
        userId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = FollowingsQuery(
            userId = userId,
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val following = response?.data?.Page?.following?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (following != null) emit(PagedResult.Success(
                data = following,
                nextPage = if (pageInfo?.hasNextPage == true)
                    pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Error"))
        }
    }
}