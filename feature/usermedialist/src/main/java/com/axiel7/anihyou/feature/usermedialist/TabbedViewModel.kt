package com.axiel7.anihyou.feature.usermedialist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.model.NovelTab
import kotlinx.coroutines.launch

class TabbedViewModel(
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    ): ViewModel() {

    fun saveNovelTab(value: Int) {
        viewModelScope.launch {
            NovelTab.valueOf(value)?.let {
                defaultPreferencesRepository.setDefaultNovelTab(it)
            }
        }
    }

}