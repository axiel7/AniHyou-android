package com.axiel7.anihyou.ui.screens.characterdetails

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.CharacterDetailsQuery
import com.axiel7.anihyou.CharacterMediaQuery
import com.axiel7.anihyou.data.repository.CharacterRepository
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.ui.base.UiState
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(
    private val characterId: Int
) : BaseViewModel() {

    var characterDetails by mutableStateOf<CharacterDetailsQuery.Character?>(null)
    val alternativeNames by derivedStateOf {
        characterDetails?.name?.alternative?.filterNotNull()?.joinToString()
    }
    val alternativeNamesSpoiler by derivedStateOf {
        characterDetails?.name?.alternativeSpoiler?.filterNotNull()?.joinToString()
    }

    suspend fun getCharacterDetails() = viewModelScope.launch {
        CharacterRepository.getCharacterDetails(characterId).collect { uiState ->
            isLoading = uiState is UiState.Loading

            if (uiState is UiState.Success) {
                characterDetails = uiState.data
            }
            else if (uiState is UiState.Error) {
                message = uiState.message
            }
        }
    }

    suspend fun toggleFavorite() {
        characterDetails?.let { details ->
            FavoriteRepository.toggleFavorite(
                characterId = characterId
            ).collect { uiState ->
                if (uiState is UiState.Success) {
                    if (uiState.data) {
                        characterDetails = details.copy(isFavourite = !details.isFavourite)
                    }
                }
            }
        }
    }

    private var page = 1
    var hasNextPage = true
    var characterMedia =  mutableStateListOf<CharacterMediaQuery.Edge>()

    suspend fun getCharacterMedia() = viewModelScope.launch {
        CharacterRepository.getCharacterMediaPage(
            characterId = characterId,
            page = page,
        ).collect { uiState ->
            isLoading = page == 1 && uiState is UiState.Loading

            if (uiState is UiState.Success) {
                uiState.data.edges?.filterNotNull()?.let { characterMedia.addAll(it) }
                page = uiState.data.pageInfo?.currentPage?.plus(1) ?: page
                hasNextPage = uiState.data.pageInfo?.hasNextPage ?: false
            }
            else if (uiState is UiState.Error) {
                message = uiState.message
            }
        }
    }
}