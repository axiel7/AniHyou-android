package com.axiel7.anihyou.feature.mediadetails.characters

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.base.state.PagedUiState
import com.axiel7.anihyou.core.network.fragment.MediaCharacter

@Stable
data class MediaCharactersUiState(
    val characters: SnapshotStateList<MediaCharacter> = mutableStateListOf(),
    val availableLanguages: List<String>? = null,
    val selectedLanguage: String? = null,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
): PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
