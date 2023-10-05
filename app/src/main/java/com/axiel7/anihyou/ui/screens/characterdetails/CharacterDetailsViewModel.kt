package com.axiel7.anihyou.ui.screens.characterdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.axiel7.anihyou.data.repository.CharacterRepository
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.ui.common.UiStateViewModel
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

@HiltViewModel
class CharacterDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    characterRepository: CharacterRepository,
    private val favoriteRepository: FavoriteRepository,
) : UiStateViewModel<CharacterDetailsUiState>() {

    private val characterId: Int = savedStateHandle[CHARACTER_ID_ARGUMENT.removeFirstAndLast()]!!

    override val mutableUiState = MutableStateFlow(CharacterDetailsUiState())
    override val uiState = mutableUiState.asStateFlow()

    init {
        characterRepository
            .getCharacterDetails(characterId)
            .onEach { result ->
                result.handleDataResult { data ->
                    mutableUiState.updateAndGet { it.copy(character = data) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleFavorite() = viewModelScope.launch {
        favoriteRepository.toggleFavorite(
            characterId = characterId
        ).collect { result ->
            if (result is DataResult.Success && result.data != null) {
                mutableUiState.update {
                    it.copy(
                        character = it.character?.copy(
                            isFavourite = it.character.isFavourite.not()
                        )
                    )
                }
            }
        }
    }

    val characterMedia = characterRepository
        .getCharacterMediaPage(characterId)
        .cachedIn(viewModelScope)
}