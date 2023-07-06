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
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.ui.base.BaseViewModel
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

    fun getCharacterDetails() = viewModelScope.launch(dispatcher) {
        CharacterRepository.getCharacterDetails(characterId).collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                characterDetails = result.data
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    fun toggleFavorite() = viewModelScope.launch(dispatcher) {
        characterDetails?.let { details ->
            FavoriteRepository.toggleFavorite(
                characterId = characterId
            ).collect { result ->
                if (result is DataResult.Success && result.data) {
                    characterDetails = details.copy(isFavourite = !details.isFavourite)
                }
            }
        }
    }

    private var page = 1
    var hasNextPage = true
    var characterMedia = mutableStateListOf<CharacterMediaQuery.Edge>()

    fun getCharacterMedia() = viewModelScope.launch(dispatcher) {
        CharacterRepository.getCharacterMediaPage(
            characterId = characterId,
            page = page,
        ).collect { result ->
            isLoading = page == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                characterMedia.addAll(result.data)
                page = result.nextPage ?: page
                hasNextPage = result.nextPage != null
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }
}