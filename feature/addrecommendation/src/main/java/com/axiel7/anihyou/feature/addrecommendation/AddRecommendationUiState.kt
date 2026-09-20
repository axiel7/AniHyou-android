package com.axiel7.anihyou.feature.addrecommendation

import androidx.compose.runtime.Stable
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.network.MediaDetailsQuery
import com.axiel7.anihyou.core.network.SearchMediaQuery
import com.axiel7.anihyou.core.network.fragment.MediaRecommended

@Stable
data class AddRecommendationUiState(
    val mediaId: Int,
    val media: MediaDetailsQuery.Media? = null,
    val recommendation: SearchMediaQuery.Medium? = null,
    val returnedRecommendation: MediaRecommended? = null,
    val isSaved: Boolean = false,
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)
}
