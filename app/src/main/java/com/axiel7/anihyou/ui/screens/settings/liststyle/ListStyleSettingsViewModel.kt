package com.axiel7.anihyou.ui.screens.settings.liststyle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.ListPreferencesRepository
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.ui.common.ListStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListStyleSettingsViewModel @Inject constructor(
    private val listPreferencesRepository: ListPreferencesRepository
) : ViewModel(), ListStyleSettingsEvent {

    override fun getAnimeListStyle(status: MediaListStatus) =
        when (status) {
            MediaListStatus.CURRENT -> listPreferencesRepository.animeCurrentListStyle
            MediaListStatus.PLANNING -> listPreferencesRepository.animePlanningListStyle
            MediaListStatus.COMPLETED -> listPreferencesRepository.animeCompletedListStyle
            MediaListStatus.DROPPED -> listPreferencesRepository.animeDroppedListStyle
            MediaListStatus.PAUSED -> listPreferencesRepository.animePausedListStyle
            MediaListStatus.REPEATING -> listPreferencesRepository.animeRepeatingListStyle
            MediaListStatus.UNKNOWN__ -> flowOf(null)
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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

    override fun getMangaListStyle(status: MediaListStatus) =
        when (status) {
            MediaListStatus.CURRENT -> listPreferencesRepository.mangaCurrentListStyle
            MediaListStatus.PLANNING -> listPreferencesRepository.mangaPlanningListStyle
            MediaListStatus.COMPLETED -> listPreferencesRepository.mangaCompletedListStyle
            MediaListStatus.DROPPED -> listPreferencesRepository.mangaDroppedListStyle
            MediaListStatus.PAUSED -> listPreferencesRepository.mangaPausedListStyle
            MediaListStatus.REPEATING -> listPreferencesRepository.mangaRepeatingListStyle
            MediaListStatus.UNKNOWN__ -> flowOf(null)
        }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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
}