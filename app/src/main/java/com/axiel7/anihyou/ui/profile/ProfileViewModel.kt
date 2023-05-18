package com.axiel7.anihyou.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.App
import com.axiel7.anihyou.UserBasicInfoQuery
import com.axiel7.anihyou.ViewerQuery
import com.axiel7.anihyou.data.PreferencesDataStore
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.fragment.UserInfo
import com.axiel7.anihyou.ui.base.BaseViewModel

class ProfileViewModel : BaseViewModel() {

    private var userId = 0
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
}