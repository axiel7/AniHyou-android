package com.axiel7.anihyou.ui.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R

sealed class BottomDestination(
    val index: Int,
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int,
) {
    object Home : BottomDestination(
        index = 0,
        route = "home",
        title = R.string.home,
        icon = R.drawable.home_24,
        iconSelected = R.drawable.home_filled_24
    )

    object AnimeList : BottomDestination(
        index = 1,
        route = "anime_list",
        title = R.string.anime,
        icon = R.drawable.live_tv_24,
        iconSelected = R.drawable.live_tv_filled_24
    )

    object MangaList : BottomDestination(
        index = 2,
        route = "manga_list",
        title = R.string.manga,
        icon = R.drawable.book_24,
        iconSelected = R.drawable.book_filled_24
    )

    object Profile : BottomDestination(
        index = 3,
        route = "profile",
        title = R.string.profile,
        icon = R.drawable.person_24,
        iconSelected = R.drawable.person_filled_24
    )

    object Explore : BottomDestination(
        index = 4,
        route = "explore",
        title = R.string.explore,
        icon = R.drawable.explore_24,
        iconSelected = R.drawable.explore_filled_24
    )

    companion object {
        val values = listOf(Home, AnimeList, MangaList, Profile, Explore)

        val railValues = listOf(Home, AnimeList, MangaList, Profile)

        @Composable
        fun BottomDestination.Icon(selected: Boolean) {
            androidx.compose.material3.Icon(
                painter = painterResource(if (selected) iconSelected else icon),
                contentDescription = stringResource(title)
            )
        }

        fun String.toBottomDestinationIndex() = values.find { it.route == this }?.index

        fun Int.toBottomDestinationRoute() = values.find { it.index == this }?.route
    }
}