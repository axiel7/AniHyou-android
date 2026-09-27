package com.axiel7.anihyou.feature.settings.customlinks

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.model.CustomLink
import com.axiel7.anihyou.core.domain.repository.CustomLinksRepository
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomLinksViewModel(
    private val customLinksRepository: CustomLinksRepository,
) : UiStateViewModel<CustomLinksUiState>(), CustomLinksEvent {

    override val initialState = CustomLinksUiState()

    override fun onLinkAdded(link: CustomLink) {
        viewModelScope.launch {
            customLinksRepository.upsertCustomLink(link)
        }
    }

    override fun onLinkRemoved(link: CustomLink) {
        viewModelScope.launch {
            customLinksRepository.deleteCustomLink(link)
        }
    }

    init {
        viewModelScope.launch {
            customLinksRepository.getAllCustomLinks(MediaType.ANIME)
                .collectLatest { value ->
                    mutableUiState.update { it.copy(animeLinks = value.toSet()) }
                }
        }

        viewModelScope.launch {
            customLinksRepository.getAllCustomLinks(MediaType.MANGA)
                .collectLatest { value ->
                    mutableUiState.update { it.copy(mangaLinks = value.toSet()) }
                }
        }
    }
}