package com.axiel7.anihyou.feature.profile.favorites.reorder

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.base.state.PagedUiState
import com.axiel7.anihyou.core.model.FavoritesType
import com.axiel7.anihyou.core.network.UserFavoritesAnimeQuery
import com.axiel7.anihyou.core.network.UserFavoritesCharacterQuery
import com.axiel7.anihyou.core.network.UserFavoritesMangaQuery
import com.axiel7.anihyou.core.network.UserFavoritesStaffQuery
import com.axiel7.anihyou.core.network.UserFavoritesStudioQuery

@Stable
data class ReorderFavoritesUiState(
    val userId: Int,
    val type: FavoritesType = FavoritesType.ANIME,
    val anime: SnapshotStateList<UserFavoritesAnimeQuery.Node> = mutableStateListOf(),
    val manga: SnapshotStateList<UserFavoritesMangaQuery.Node> = mutableStateListOf(),
    val characters: SnapshotStateList<UserFavoritesCharacterQuery.Node> = mutableStateListOf(),
    val staff: SnapshotStateList<UserFavoritesStaffQuery.Node> = mutableStateListOf(),
    val studios: SnapshotStateList<UserFavoritesStudioQuery.Node> = mutableStateListOf(),
    val fetchFromNetwork: Boolean = false,
    val isSaved: Boolean = false,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
    override fun setPage(value: Int) = copy(page = value)

    val getList: List<Any> get() = when (type) {
        FavoritesType.ANIME -> anime
        FavoritesType.MANGA -> manga
        FavoritesType.CHARACTERS -> characters
        FavoritesType.STAFF -> staff
        FavoritesType.STUDIOS -> studios
    }
}