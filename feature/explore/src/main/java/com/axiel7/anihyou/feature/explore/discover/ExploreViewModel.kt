package com.axiel7.anihyou.feature.explore.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.model.ExploreTab
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : ViewModel() {

    fun saveExploreTab(value: Int) {
        viewModelScope.launch {
            ExploreTab.valueOf(value)?.let {
                defaultPreferencesRepository.setDefaultExploreTab(it)
            }
        }
    }
}