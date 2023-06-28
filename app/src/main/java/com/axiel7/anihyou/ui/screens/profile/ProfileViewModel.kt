package com.axiel7.anihyou.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.App
import com.axiel7.anihyou.UserActivityQuery
import com.axiel7.anihyou.data.PreferencesDataStore
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.fragment.UserInfo
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ProfileViewModel : BaseViewModel() {

    var userId = 0
    var userInfo by mutableStateOf<UserInfo?>(null)

    suspend fun getMyUserInfo() = viewModelScope.launch {
        UserRepository.getMyUserInfo().collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                userId = result.data.id
                userInfo = result.data
                // refresh user options
                App.dataStore.edit {
                    it[PreferencesDataStore.PROFILE_COLOR_PREFERENCE_KEY] =
                        result.data.options?.profileColor ?: "#526CFD"
                    it[PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY] =
                        result.data.mediaListOptions?.scoreFormat?.name ?: "POINT_10"
                }
            }
            else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    suspend fun getUserInfo(
        userId: Int? = null,
        username: String? = null,
    ) = viewModelScope.launch {
        UserRepository.getUserInfo(
            userId = userId,
            username = username,
        ).collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                this@ProfileViewModel.userId = result.data.id
                userInfo = result.data
            }
            else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    suspend fun toggleFollow() = viewModelScope.launch {
        UserRepository.toggleFollow(userId).collect { result ->
            if (result is DataResult.Success) {
                userInfo = userInfo?.copy(isFollowing = result.data.isFollowing)
            }
            else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    var isLoadingActivity by mutableStateOf(false)
    private var pageActivity = 1
    var hasNextPageActivity = true
    var userActivities = mutableStateListOf<UserActivityQuery.Activity>()

    suspend fun getUserActivity(userId: Int) = viewModelScope.launch {
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
}