package com.axiel7.anihyou.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.App
import com.axiel7.anihyou.UserActivityQuery
import com.axiel7.anihyou.UserBasicInfoQuery
import com.axiel7.anihyou.ViewerQuery
import com.axiel7.anihyou.data.PreferencesDataStore
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.fragment.UserInfo
import com.axiel7.anihyou.type.ActivitySort
import com.axiel7.anihyou.ui.base.BaseViewModel

class ProfileViewModel : BaseViewModel() {

    var userId = 0
    var userInfo by mutableStateOf<UserInfo?>(null)

    suspend fun getMyUserInfo() {
        isLoading = true
        userId = LoginRepository.getUserId() ?: 0
        val response = ViewerQuery().tryQuery()

        response?.data?.Viewer?.userInfo?.let { info ->
            userInfo = info
            // refresh user options
            App.dataStore.edit {
                it[PreferencesDataStore.PROFILE_COLOR_PREFERENCE_KEY] = info.options?.profileColor ?: "#526CFD"
                it[PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY] = info.mediaListOptions?.scoreFormat?.name ?: "POINT_10"
            }
        }
        isLoading = false
    }

    suspend fun getUserInfo(userId: Int) {
        isLoading = true
        this.userId = userId
        val response = UserBasicInfoQuery(
            userId = Optional.present(userId)
        ).tryQuery()

        response?.data?.User?.userInfo?.let { userInfo = it }

        isLoading = false
    }

    suspend fun getUserInfo(username: String) {
        isLoading = true
        val response = UserBasicInfoQuery(
            name = Optional.present(username)
        ).tryQuery()

        response?.data?.User?.userInfo?.let {
            userInfo = it
            userId = it.id
        }

        isLoading = false
    }

    var isLoadingActivity by mutableStateOf(false)
    var pageActivity = 1
    var hasNextPageActivity = true
    var userActivities = mutableStateListOf<UserActivityQuery.Activity>()

    suspend fun getUserActivity(userId: Int) {
        isLoadingActivity = pageActivity == 1
        val response = UserActivityQuery(
            page = Optional.present(pageActivity),
            perPage = Optional.present(25),
            userId = Optional.present(userId),
            sort = Optional.present(listOf(ActivitySort.ID_DESC))
        ).tryQuery()

        response?.data?.Page?.activities?.filterNotNull()?.let { userActivities.addAll(it) }
        hasNextPageActivity = response?.data?.Page?.pageInfo?.hasNextPage ?: false
        pageActivity = response?.data?.Page?.pageInfo?.currentPage?.plus(1) ?: pageActivity

        isLoadingActivity = false
    }
}