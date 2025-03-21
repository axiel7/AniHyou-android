package com.axiel7.anihyou.ui.screens.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.common.ANIHYOU_SCHEME
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.LoginRepository
import com.axiel7.anihyou.core.model.DefaultTab
import com.axiel7.anihyou.core.network.NetworkVariables
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

    fun onIntentDataReceived(data: Uri?) = viewModelScope.launch {
        if (data?.scheme == ANIHYOU_SCHEME) {
            loginRepository.parseRedirectUri(data)
        }
    }

    init {
        accessToken
            .onEach { setToken(it) }
            .launchIn(viewModelScope)
    }
}