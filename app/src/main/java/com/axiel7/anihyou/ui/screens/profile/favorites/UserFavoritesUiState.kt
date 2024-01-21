package com.axiel7.anihyou.ui.screens.profile.favorites

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.UserFavoritesAnimeQuery
import com.axiel7.anihyou.UserFavoritesCharacterQuery
import com.axiel7.anihyou.UserFavoritesMangaQuery
import com.axiel7.anihyou.UserFavoritesStaffQuery
import com.axiel7.anihyou.UserFavoritesStudioQuery
import com.axiel7.anihyou.ui.common.state.PagedUiState

@Stable
data class UserFavoritesUiState(
    val userId: Int? = null,
    val type: FavoritesType = FavoritesType.ANIME,
    val anime: SnapshotStateList<UserFavoritesAnimeQuery.Node> = mutableStateListOf(),
    val manga: SnapshotStateList<UserFavoritesMangaQuery.Node> = mutableStateListOf(),
    val characters: SnapshotStateList<UserFavoritesCharacterQuery.Node> = mutableStateListOf(),
    val staff: SnapshotStateList<UserFavoritesStaffQuery.Node> = mutableStateListOf(),
    val studios: SnapshotStateList<UserFavoritesStudioQuery.Node> = mutableStateListOf(),
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
    override fun setPage(value: Int) = copy(page = value)
}
