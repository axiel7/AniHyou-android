package com.axiel7.anihyou.ui.screens.main

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.ANIHYOU_AUTH_RESPONSE
import com.axiel7.anihyou.core.base.ANIHYOU_SCHEME
import com.axiel7.anihyou.core.base.ANIHYOU_WEAR_AUTH
import com.axiel7.anihyou.core.base.ANIHYOU_WEAR_CALLBACK_URL
import com.axiel7.anihyou.core.common.utils.ContextUtils.showToast
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.LoginRepository
import com.axiel7.anihyou.core.model.DefaultTab
import com.axiel7.anihyou.core.network.NetworkVariables
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.startRemoteActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val networkVariables: NetworkVariables,
    private val loginRepository: LoginRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : ViewModel(), MainEvent {

    val accessToken = defaultPreferencesRepository.accessToken

    val isLoggedIn = defaultPreferencesRepository.isLoggedIn

    val homeTab = defaultPreferencesRepository.defaultHomeTab

    val theme = defaultPreferencesRepository.theme

    val useBlackColors = defaultPreferencesRepository.useBlackColors

    val appColor = defaultPreferencesRepository.appColor

    val appColorMode = defaultPreferencesRepository.appColorMode

    override fun saveLastTab(index: Int) {
        viewModelScope.launch {
            defaultPreferencesRepository.setLastTab(index)
        }
    }

    suspend fun getStartTab(): Int {
        val defaultTab = defaultPreferencesRepository.defaultTab.first()
        return if (defaultTab == null || defaultTab == DefaultTab.LAST_USED) {
            defaultPreferencesRepository.lastTab.first()
        } else {
            defaultTab.ordinal - 1
        }
    }

    fun setToken(token: String?) {
        networkVariables.accessToken = token
    }

    fun onIntentDataReceived(context: Context, data: Uri?) = viewModelScope.launch {
        if (data?.scheme == ANIHYOU_SCHEME) {
            when {
                data.toString().contains(ANIHYOU_AUTH_RESPONSE) -> loginRepository.parseRedirectUri(data)
                data.toString().contains(ANIHYOU_WEAR_AUTH) -> sendAuthTokenToWearable(context)
            }
        }
    }

    private fun sendAuthTokenToWearable(context: Context) {
        viewModelScope.launch {
            val token = accessToken.first()
            if (token == null) {
                context.showToast(R.string.not_logged_text)
            } else {
                val data = "${ANIHYOU_WEAR_CALLBACK_URL}?access_token=$token".toUri()
                context.startRemoteActivity(data)
            }
        }
    }

    init {
        accessToken
            .onEach { setToken(it) }
            .launchIn(viewModelScope)
    }
}