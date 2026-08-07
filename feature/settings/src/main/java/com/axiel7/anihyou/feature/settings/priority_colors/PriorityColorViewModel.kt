package com.axiel7.anihyou.feature.settings.priority_colors

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PriorityColorViewModel(
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<PriorityColorUiState>(), PriorityColorEvent {

    override val initialState = PriorityColorUiState()

    override fun onHighPriorityColorChanged(color: Color) {
        mutableUiState.update { it.copy(highPriorityColor = color) }
    }

    override fun onMediumPriorityColorChanged(color: Color) {
        mutableUiState.update { it.copy(mediumPriorityColor = color) }
    }

    override fun onLowPriorityColorChanged(color: Color) {
        mutableUiState.update { it.copy(lowPriorityColor = color) }
    }

    override fun updateColors(colorLow: Color, colorMedium: Color, colorHigh: Color) {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true) }
            defaultPreferencesRepository.setColorLowPriority(colorLow)
            defaultPreferencesRepository.setColorMediumPriority(colorMedium)
            defaultPreferencesRepository.setColorHighPriority(colorHigh)
            mutableUiState.update { it.copy(isLoading = false) }
        }
    }

    // set the correct colors from the settings
    init {
        defaultPreferencesRepository.colorLowPriority
            .onEach { color ->
                mutableUiState.update { it.copy(lowPriorityColor = Color(color)) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.colorMediumPriority
            .onEach { color ->
                mutableUiState.update { it.copy(mediumPriorityColor = Color(color)) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.colorHighPriority
            .onEach { color ->
                mutableUiState.update { it.copy(highPriorityColor = Color(color)) }
            }
            .launchIn(viewModelScope)
    }
}
