package com.axiel7.anihyou.feature.settings.customlists

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.UserRepository
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomListsViewModel(
    private val userRepository: UserRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<CustomListsUiState>(), CustomListsEvent {

    override val initialState = CustomListsUiState()

    override fun onListAdded(list: String, mediaType: MediaType) {
        mutableUiState.value.run {
            if (mediaType == MediaType.ANIME) {
                animeLists.add(list)
            } else if (mediaType == MediaType.MANGA) {
                mangaLists.add(list)
            }
        }
    }

    override fun onListRemoved(list: String, mediaType: MediaType) {
        mutableUiState.value.run {
            if (mediaType == MediaType.ANIME) {
                animeLists.remove(list)
            } else if (mediaType == MediaType.MANGA) {
                mangaLists.remove(list)
            }
        }
    }

    override fun onListEdited(prev: String, new: String, mediaType: MediaType) {
        mutableUiState.value.run {
            if (mediaType == MediaType.ANIME) {
                val index = animeLists.indexOf(prev)
                if (index != -1) animeLists[index] = new
            } else if (mediaType == MediaType.MANGA) {
                val index = mangaLists.indexOf(prev)
                if (index != -1) mangaLists[index] = new
            }
        }
    }

    override fun updateCustomLists() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true) }
            userRepository.updateCustomLists(
                animeList = mutableUiState.value.animeLists,
                mangaList = mutableUiState.value.mangaLists,
            ).let { result ->
                if (result is DataResult.Success) {
                    defaultPreferencesRepository.saveAnimeCustomLists(
                        result.data?.mediaListOptions?.animeList?.customLists?.filterNotNull()
                            .orEmpty()
                    )
                    defaultPreferencesRepository.saveMangaCustomLists(
                        result.data?.mediaListOptions?.mangaList?.customLists?.filterNotNull()
                            .orEmpty()
                    )
                } else {
                    mutableUiState.update { result.toUiState() }
                }
            }
            mutableUiState.update { it.copy(isLoading = false) }
        }
    }

    init {
        viewModelScope.launch {
            defaultPreferencesRepository.animeCustomLists
                .filterNotNull()
                .first().let { value ->
                    mutableUiState.value.animeLists.apply {
                        clear()
                        addAll(value)
                    }
                }
        }
        viewModelScope.launch {
            defaultPreferencesRepository.mangaCustomLists
                .filterNotNull()
                .first().let { value ->
                    mutableUiState.value.mangaLists.apply {
                        clear()
                        addAll(value)
                    }
                }
        }
    }
}