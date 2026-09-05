package com.axiel7.anihyou.feature.settings.customlinks

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.network.type.MediaType

@Immutable
data class CustomLinksUiState(
    val animeLinks: Set<String> = emptySet(),
    val mangaLinks: Set<String> = emptySet(),
    override val isLoading: Boolean = false,
    override val error: String? = null,
): UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)

    fun customLinks(type: MediaType) = when (type) {
        MediaType.ANIME -> animeLinks
        MediaType.MANGA -> mangaLinks
        else -> null
    }
}
