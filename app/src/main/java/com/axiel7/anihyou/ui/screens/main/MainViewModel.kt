package com.axiel7.anihyou.ui.screens.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.App
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.utils.ANIHYOU_SCHEME
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : ViewModel() {

    fun onIntentDataReceived(data: Uri?) = viewModelScope.launch {
        if (data?.scheme == ANIHYOU_SCHEME) {
            loginRepository.parseRedirectUri(data)
        }
    }

    val accessToken = defaultPreferencesRepository.accessToken
        .stateIn(viewModelScope, SharingStarted.Eagerly, App.accessToken)

    val startTab = defaultPreferencesRepository.lastTab

    val homeTab = defaultPreferencesRepository.defaultHomeTab

    val theme = defaultPreferencesRepository.theme
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val appColor = defaultPreferencesRepository.appColor
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val appColorMode = defaultPreferencesRepository.appColorMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
        .filterNotNull()

    fun saveLastTab(index: Int) = viewModelScope.launch {
        defaultPreferencesRepository.setLastTab(index)
    }
}