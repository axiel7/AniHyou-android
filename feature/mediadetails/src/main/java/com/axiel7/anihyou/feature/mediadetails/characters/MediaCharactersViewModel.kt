package com.axiel7.anihyou.feature.mediadetails.characters

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.ui.common.navigation.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.InjectedParam

@OptIn(ExperimentalCoroutinesApi::class)
@Stable
class MediaCharactersViewModel(
    @InjectedParam arguments: Route.MediaCharacters,
    mediaRepository: MediaRepository,
) : PagedUiStateViewModel<MediaCharactersUiState>(), MediaCharactersEvent {

    override val initialState = MediaCharactersUiState()

    override fun onLanguageSelect(language: String) {
        mutableUiState.update { it.copy(selectedLanguage = language) }
    }

    init {
        mutableUiState
            .filter { it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
            }
            .flatMapLatest { uiState ->
                mediaRepository.getMediaCharactersPage(
                    mediaId = arguments.mediaId,
                    page = uiState.page,
                )
            }
            .onEach { result ->
                if (result is PagedResult.Success) {
                    mutableUiState.update { uiState ->
                        var availableLanguages: List<String>? = null
                        if (uiState.page == 1) {
                            uiState.characters.clear()
                            availableLanguages = result.list.firstOrNull()?.voiceActors
                                ?.mapNotNull { it?.commonVoiceActor?.languageV2 }
                        }
                        uiState.characters.addAll(result.list)
                        uiState.copy(
                            availableLanguages = availableLanguages ?: uiState.availableLanguages,
                            selectedLanguage = availableLanguages?.firstOrNull() ?: uiState.selectedLanguage,
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                        )
                    }
                } else {
                    mutableUiState.update {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}