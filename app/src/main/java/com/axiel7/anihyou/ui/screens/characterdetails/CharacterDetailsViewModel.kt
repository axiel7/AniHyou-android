package com.axiel7.anihyou.ui.screens.characterdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.CharacterMediaQuery
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.repository.CharacterRepository
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.common.navigation.NavArgument
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val characterRepository: CharacterRepository,
    private val favoriteRepository: FavoriteRepository,
) : PagedUiStateViewModel<CharacterDetailsUiState>(), CharacterDetailsEvent {

    private val characterId =
        savedStateHandle.getStateFlow<Int?>(NavArgument.CharacterId.name, null)

    override val initialState = CharacterDetailsUiState()

    override fun toggleFavorite() {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(
                characterId = characterId.value
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
    }

    override fun selectMediaItem(value: CharacterMediaQuery.Edge?) {
        mutableUiState.update {
            it.copy(selectedMediaItem = value)
        }
    }

    override fun onShowVoiceActorsSheet(item: CharacterMediaQuery.Edge) {
        mutableUiState.update {
            it.copy(selectedMediaVoiceActors = item.voiceActors?.filterNotNull())
        }
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        mutableUiState.value.run {
            selectedMediaItem?.let { selectedItem ->
                val index = media.indexOf(selectedItem)
                if (index != -1) {
                    media[index] = selectedItem.copy(
                        node = selectedItem.node?.copy(
                            mediaListEntry = newListEntry?.let {
                                CharacterMediaQuery.MediaListEntry(
                                    __typename = "CharacterMediaQuery.MediaListEntry",
                                    id = newListEntry.id,
                                    mediaId = newListEntry.mediaId,
                                    basicMediaListEntry = newListEntry
                                )
                            }
                        )
                    )
                }
            }
        }
    }

    init {
        characterId
            .filterNotNull()
            .flatMapLatest { characterId ->
                characterRepository.getCharacterDetails(characterId)
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            isLoading = false,
                            character = result.data
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)

        mutableUiState
            .filter { it.hasNextPage && it.page != 0 }
            .distinctUntilChangedBy { it.page }
            .combine(characterId.filterNotNull(), ::Pair)
            .flatMapLatest { (uiState, characterId) ->
                characterRepository.getCharacterMediaPage(
                    characterId = characterId,
                    page = uiState.page,
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.media.addAll(result.list)
                        it.copy(
                            isLoadingMedia = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState(loadingWhen = false)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}