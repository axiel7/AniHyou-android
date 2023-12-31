package com.axiel7.anihyou.ui.screens.characterdetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.CharacterDetailsQuery
import com.axiel7.anihyou.CharacterMediaQuery
import com.axiel7.anihyou.ui.common.state.PagedUiState

@Immutable
data class CharacterDetailsUiState(
    val character: CharacterDetailsQuery.Character? = null,
    val isLoadingMedia: Boolean = true,
    val selectedMediaItem: CharacterMediaQuery.Edge? = null,
    val selectedMediaVoiceActors: List<CharacterMediaQuery.VoiceActor>? = null,
    override val page: Int = 0,
    override val hasNextPage: Boolean = true,
    override val isLoading: Boolean = true,
    override val error: String? = null,
) : PagedUiState<CharacterDetailsUiState> {

    val alternativeNames
        get() = character?.name?.alternative?.filterNotNull()?.joinToString()
    val alternativeNamesSpoiler
        get() = character?.name?.alternativeSpoiler?.filterNotNull()?.joinToString()

    override fun setError(value: String?) = copy(error = value)

    override fun setLoading(value: Boolean) = copy(isLoading = value)

    override fun setPage(value: Int) = copy(page = value)

    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
