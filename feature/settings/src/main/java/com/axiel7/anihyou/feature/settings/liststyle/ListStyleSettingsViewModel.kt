package com.axiel7.anihyou.feature.settings.liststyle

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.domain.repository.ListPreferencesRepository
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListStyleSettingsViewModel(
    private val listPreferencesRepository: ListPreferencesRepository
) : UiStateViewModel<ListStyleSettingsUiState>(), ListStyleSettingsEvent {

    override val initialState = ListStyleSettingsUiState()

    override fun setAnimeListStyle(status: MediaListStatus, value: ListStyle) {
        viewModelScope.launch {
            when (status) {
                MediaListStatus.CURRENT -> listPreferencesRepository.setAnimeCurrentListStyle(value)
                MediaListStatus.PLANNING -> listPreferencesRepository.setAnimePlanningListStyle(value)
                MediaListStatus.COMPLETED -> listPreferencesRepository.setAnimeCompletedListStyle(value)
                MediaListStatus.DROPPED -> listPreferencesRepository.setAnimeDroppedListStyle(value)
                MediaListStatus.PAUSED -> listPreferencesRepository.setAnimePausedListStyle(value)
                MediaListStatus.REPEATING -> listPreferencesRepository.setAnimeRepeatingListStyle(value)
                MediaListStatus.UNKNOWN__ -> {}
            }
        }
    }

    override fun setMangaListStyle(status: MediaListStatus, value: ListStyle) {
        viewModelScope.launch {
            when (status) {
                MediaListStatus.CURRENT -> listPreferencesRepository.setMangaCurrentListStyle(value)
                MediaListStatus.PLANNING -> listPreferencesRepository.setMangaPlanningListStyle(value)
                MediaListStatus.COMPLETED -> listPreferencesRepository.setMangaCompletedListStyle(value)
                MediaListStatus.DROPPED -> listPreferencesRepository.setMangaDroppedListStyle(value)
                MediaListStatus.PAUSED -> listPreferencesRepository.setMangaPausedListStyle(value)
                MediaListStatus.REPEATING -> listPreferencesRepository.setMangaRepeatingListStyle(value)
                MediaListStatus.UNKNOWN__ -> {}
            }
        }
    }

    init {
        combine(
            listPreferencesRepository.animeCurrentListStyle,
            listPreferencesRepository.animePlanningListStyle,
            listPreferencesRepository.animeCompletedListStyle,
            listPreferencesRepository.animeDroppedListStyle,
            listPreferencesRepository.animePausedListStyle,
            listPreferencesRepository.animeRepeatingListStyle
        ) { values ->
            mutableUiState.update {
                it.copy(
                    animeCurrentListStyle = values[0],
                    animePlanningListStyle = values[1],
                    animeCompletedListStyle = values[2],
                    animeDroppedListStyle = values[3],
                    animePausedListStyle = values[4],
                    animeRepeatingListStyle = values[5]
                )
            }
        }.launchIn(viewModelScope)

        combine(
            listPreferencesRepository.mangaCurrentListStyle,
            listPreferencesRepository.mangaPlanningListStyle,
            listPreferencesRepository.mangaCompletedListStyle,
            listPreferencesRepository.mangaDroppedListStyle,
            listPreferencesRepository.mangaPausedListStyle,
            listPreferencesRepository.mangaRepeatingListStyle
        ) { values ->
            mutableUiState.update {
                it.copy(
                    mangaCurrentListStyle = values[0],
                    mangaPlanningListStyle = values[1],
                    mangaCompletedListStyle = values[2],
                    mangaDroppedListStyle = values[3],
                    mangaPausedListStyle = values[4],
                    mangaRepeatingListStyle = values[5]
                )
            }
        }.launchIn(viewModelScope)
    }
}