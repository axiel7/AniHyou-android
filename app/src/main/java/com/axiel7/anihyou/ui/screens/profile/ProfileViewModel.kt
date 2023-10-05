package com.axiel7.anihyou.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.App
import com.axiel7.anihyou.UserActivityQuery
import com.axiel7.anihyou.data.PreferencesDataStore.APP_COLOR_MODE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.APP_COLOR_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.PROFILE_COLOR_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY
import com.axiel7.anihyou.data.model.activity.updateLikeStatus
import com.axiel7.anihyou.data.model.user.hexColor
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.fragment.UserInfo
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.common.AppColorMode
import com.axiel7.anihyou.ui.common.UiStateViewModel
import kotlinx.coroutines.launch

class ProfileViewModel : UiStateViewModel() {

    var userId = 0
    var userInfo by mutableStateOf<UserInfo?>(null)

    fun getMyUserInfo() = viewModelScope.launch(dispatcher) {
        UserRepository.getMyUserInfo().collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                userId = result.data.id
                userInfo = result.data
                // refresh user options
                App.dataStore.edit {
                    val profileColor = result.data.hexColor()
                    it[PROFILE_COLOR_PREFERENCE_KEY] = profileColor
                    it[SCORE_FORMAT_PREFERENCE_KEY] =
                        result.data.mediaListOptions?.scoreFormat?.name ?: "POINT_10"
                    if (it[APP_COLOR_MODE_PREFERENCE_KEY] == AppColorMode.PROFILE.name) {
                        it[APP_COLOR_PREFERENCE_KEY] = profileColor
                    }
                }
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    fun getUserInfo(
        userId: Int? = null,
        username: String? = null,
    ) = viewModelScope.launch(dispatcher) {
        UserRepository.getUserInfo(
            userId = userId,
            username = username,
        ).collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                this@ProfileViewModel.userId = result.data.id
                userInfo = result.data
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    fun toggleFollow() = viewModelScope.launch(dispatcher) {
        UserRepository.toggleFollow(userId).collect { result ->
            if (result is DataResult.Success) {
                userInfo = userInfo?.copy(isFollowing = result.data.isFollowing)
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    var isLoadingActivity by mutableStateOf(false)
    private var pageActivity = 1
    var hasNextPageActivity = true
    var userActivities = mutableStateListOf<UserActivityQuery.Activity>()

    fun getUserActivity(userId: Int) = viewModelScope.launch(dispatcher) {
        UserRepository.getUserActivity(
            userId = userId,
            page = pageActivity
        ).collect { result ->
            isLoadingActivity = pageActivity == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                userActivities.addAll(result.data)
                hasNextPageActivity = result.nextPage != null
                pageActivity = result.nextPage ?: pageActivity
            }
        }
    }

    fun toggleLikeActivity(id: Int) = viewModelScope.launch(dispatcher) {
        LikeRepository.toggleLike(
            likeableId = id,
            type = LikeableType.ACTIVITY
        ).collect { result ->
            if (result is DataResult.Success) {
                val isLiked = result.data
                val foundIndex = userActivities.indexOfFirst {
                    it.onListActivity?.listActivityFragment?.id == id
                            || it.onTextActivity?.textActivityFragment?.id == id
                }
                if (foundIndex != -1) {
                    val oldItem = userActivities[foundIndex]
                    userActivities[foundIndex] = oldItem.copy(
                        onTextActivity = oldItem.onTextActivity?.copy(
                            textActivityFragment = oldItem.onTextActivity.textActivityFragment
                                .updateLikeStatus(isLiked)
                        ),
                        onListActivity = oldItem.onListActivity?.copy(
                            listActivityFragment = oldItem.onListActivity.listActivityFragment
                                .updateLikeStatus(isLiked)
                        )
                    )
                }
            }
        }
    }
}