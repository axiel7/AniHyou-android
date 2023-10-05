package com.axiel7.anihyou.ui.screens.characterdetails

import com.axiel7.anihyou.CharacterDetailsQuery
import com.axiel7.anihyou.ui.common.UiState

data class CharacterDetailsUiState(
    val character: CharacterDetailsQuery.Character? = null,
    override val isLoading: Boolean = true,
    override val error: String? = null,
) : UiState<CharacterDetailsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)

    val alternativeNames
        get() = character?.name?.alternative?.filterNotNull()?.joinToString()
    val alternativeNamesSpoiler
        get() = character?.name?.alternativeSpoiler?.filterNotNull()?.joinToString()
}
