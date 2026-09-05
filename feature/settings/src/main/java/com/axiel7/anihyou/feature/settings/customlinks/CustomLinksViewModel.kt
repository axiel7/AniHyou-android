package com.axiel7.anihyou.feature.settings.customlinks

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomLinksViewModel(
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<CustomLinksUiState>(), CustomLinksEvent {

    override val initialState = CustomLinksUiState()

    override fun onLinkAdded(link: String, mediaType: MediaType) {
        viewModelScope.launch {
            mutableUiState.value.run {
                customLinks(mediaType)?.let {
                    setLinks(it + link, mediaType)
                }
            }
        }
    }

    override fun onLinkRemoved(link: String, mediaType: MediaType) {
        viewModelScope.launch {
            mutableUiState.value.run {
                customLinks(mediaType)?.let {
                    setLinks(it - link, mediaType)
                }
            }
        }
    }

    override fun onLinkEdited(prev: String, new: String, mediaType: MediaType) {
        viewModelScope.launch {
            mutableUiState.value.run {
                customLinks(mediaType)?.let {
                    setLinks(it - prev + new, mediaType)
                }
            }
        }
    }

    private suspend fun setLinks(links: Set<String>, mediaType: MediaType) {
        when (mediaType) {
            MediaType.ANIME ->
                defaultPreferencesRepository.setAnimeCustomLinks(links)

            MediaType.MANGA ->
                defaultPreferencesRepository.setMangaCustomLinks(links)

            else -> {}
        }
    }

    init {
        defaultPreferencesRepository.animeCustomLinks
            .filterNotNull()
            .onEach { value ->
                mutableUiState.update { it.copy(animeLinks = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.mangaCustomLinks
            .filterNotNull()
            .onEach { value ->
                mutableUiState.update { it.copy(mangaLinks = value) }
            }
            .launchIn(viewModelScope)
    }
}