package com.axiel7.anihyou.feature.profile.favorites

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.model.FavoritesType
import com.axiel7.anihyou.core.network.UserFavoritesAnimeQuery
import com.axiel7.anihyou.core.network.UserFavoritesCharacterQuery
import com.axiel7.anihyou.core.network.UserFavoritesMangaQuery
import com.axiel7.anihyou.core.network.UserFavoritesStaffQuery
import com.axiel7.anihyou.core.network.UserFavoritesStudioQuery
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.composables.person.PersonItemVertical
import com.axiel7.anihyou.core.ui.composables.person.PersonItemVerticalPlaceholder
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyGridState

internal fun LazyGridScope.favoritesItems(
    type: FavoritesType,
    anime: List<UserFavoritesAnimeQuery.Edge>,
    manga: List<UserFavoritesMangaQuery.Edge>,
    characters: List<UserFavoritesCharacterQuery.Edge>,
    staff: List<UserFavoritesStaffQuery.Edge>,
    studios: List<UserFavoritesStudioQuery.Edge>,
    isLoading: Boolean,
    blurAdult: Boolean,
    reorderableState: ReorderableLazyGridState? = null,
    onMediaClick: (Int) -> Unit = {},
    onCharacterClick: (Int) -> Unit = {},
    onStaffClick: (Int) -> Unit = {},
    onStudioClick: (Int) -> Unit = {}
) {
    when (type) {
        FavoritesType.ANIME -> {
            items(
                items = anime,
                key = { "${it.id}${it.node?.id}" },
                contentType = { type.name }
            ) { item ->
                if (reorderableState != null) {
                    ReorderableItem(reorderableState, key = "${item.id}${item.node?.id}") {
                        MediaItemVertical(
                            title = item.node?.title?.userPreferred.orEmpty(),
                            imageUrl = item.node?.coverImage?.large,
                            blurImage = blurAdult && item.node?.isAdult == true,
                            modifier = Modifier
                                .wrapContentWidth()
                                .longPressDraggableHandle(),
                            onClick = { onMediaClick(item.node?.id ?: 0) }
                        )
                    }
                } else {
                    MediaItemVertical(
                        title = item.node?.title?.userPreferred.orEmpty(),
                        imageUrl = item.node?.coverImage?.large,
                        blurImage = blurAdult && item.node?.isAdult == true,
                        modifier = Modifier.wrapContentWidth(),
                        onClick = { onMediaClick(item.node?.id ?: 0) }
                    )
                }
            }
            if (isLoading) {
                items(14) {
                    MediaItemVerticalPlaceholder()
                }
            }
        }

        FavoritesType.MANGA -> {
            items(
                items = manga,
                key = { "${it.id}${it.node?.id}" },
                contentType = { type.name }
            ) { item ->
                if (reorderableState != null) {
                    ReorderableItem(reorderableState, key = "${item.id}${item.node?.id}") {
                        MediaItemVertical(
                            title = item.node?.title?.userPreferred.orEmpty(),
                            imageUrl = item.node?.coverImage?.large,
                            blurImage = blurAdult && item.node?.isAdult == true,
                            modifier = Modifier
                                .wrapContentWidth()
                                .longPressDraggableHandle(),
                            onClick = { onMediaClick(item.node?.id ?: 0) }
                        )
                    }
                } else {
                    MediaItemVertical(
                        title = item.node?.title?.userPreferred.orEmpty(),
                        imageUrl = item.node?.coverImage?.large,
                        blurImage = blurAdult && item.node?.isAdult == true,
                        modifier = Modifier.wrapContentWidth(),
                        onClick = { onMediaClick(item.node?.id ?: 0) }
                    )
                }
            }
            if (isLoading) {
                items(14) {
                    MediaItemVerticalPlaceholder()
                }
            }
        }

        FavoritesType.CHARACTERS -> {
            items(
                items = characters,
                key = { "${it.id}${it.node?.id}" },
                contentType = { type.name }
            ) { item ->
                if (reorderableState != null) {
                    ReorderableItem(reorderableState, key = "${item.id}${item.node?.id}") {
                        PersonItemVertical(
                            title = item.node?.name?.userPreferred.orEmpty(),
                            imageUrl = item.node?.image?.large,
                            modifier = Modifier.longPressDraggableHandle(),
                            onClick = { onCharacterClick(item.node?.id ?: 0) }
                        )
                    }
                } else {
                    PersonItemVertical(
                        title = item.node?.name?.userPreferred.orEmpty(),
                        imageUrl = item.node?.image?.large,
                        onClick = { onCharacterClick(item.node?.id ?: 0) }
                    )
                }
            }
            if (isLoading) {
                items(14) {
                    PersonItemVerticalPlaceholder()
                }
            }
        }

        FavoritesType.STAFF -> {
            items(
                items = staff,
                key = { "${it.id}${it.node?.id}" },
                contentType = { type.name }
            ) { item ->
                if (reorderableState != null) {
                    ReorderableItem(reorderableState, key = "${item.id}${item.node?.id}") {
                        PersonItemVertical(
                            title = item.node?.name?.userPreferred.orEmpty(),
                            imageUrl = item.node?.image?.large,
                            modifier = Modifier.longPressDraggableHandle(),
                            onClick = { onStaffClick(item.node?.id ?: 0) }
                        )
                    }
                } else {
                    PersonItemVertical(
                        title = item.node?.name?.userPreferred.orEmpty(),
                        imageUrl = item.node?.image?.large,
                        onClick = { onStaffClick(item.node?.id ?: 0) }
                    )
                }
            }
            if (isLoading) {
                items(14) {
                    PersonItemVerticalPlaceholder()
                }
            }
        }

        FavoritesType.STUDIOS -> {
            items(
                items = studios,
                key = { "${it.id}${it.node?.id}" },
                contentType = { type.name }
            ) { item ->
                if (reorderableState != null) {
                    ReorderableItem(reorderableState, key = "${item.id}${item.node?.id}") {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .longPressDraggableHandle(),
                            onClick = { onStudioClick(item.node?.id ?: 0) }
                        ) {
                            Text(
                                text = item.node?.name.orEmpty(),
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                fontSize = 16.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        onClick = { onStudioClick(item.node?.id ?: 0) }
                    ) {
                        Text(
                            text = item.node?.name.orEmpty(),
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            fontSize = 16.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            if (isLoading) {
                items(14) {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .defaultPlaceholder(visible = true),
                    ) {
                        Text(
                            text = stringResource(R.string.loading),
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                            fontSize = 16.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
