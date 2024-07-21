package com.axiel7.anihyou.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.screens.usermedialist.AnimeTab
import com.axiel7.anihyou.ui.screens.usermedialist.MangaTab

sealed class BottomDestination(
    val index: Int,
    val route: Any,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int,
) {
    data object Home : BottomDestination(
        index = 0,
        route = com.axiel7.anihyou.ui.screens.home.Home,
        title = R.string.home,
        icon = R.drawable.home_24,
        iconSelected = R.drawable.home_filled_24
    )

    data object AnimeList : BottomDestination(
        index = 1,
        route = AnimeTab,
        title = R.string.anime,
        icon = R.drawable.live_tv_24,
        iconSelected = R.drawable.live_tv_filled_24
    )

    data object MangaList : BottomDestination(
        index = 2,
        route = MangaTab,
        title = R.string.manga,
        icon = R.drawable.book_24,
        iconSelected = R.drawable.book_filled_24
    )

    data object Profile : BottomDestination(
        index = 3,
        route = com.axiel7.anihyou.ui.screens.profile.Profile,
        title = R.string.profile,
        icon = R.drawable.person_24,
        iconSelected = R.drawable.person_filled_24
    )

    data object Explore : BottomDestination(
        index = 4,
        route = com.axiel7.anihyou.ui.screens.explore.Explore,
        title = R.string.explore,
        icon = R.drawable.explore_24,
        iconSelected = R.drawable.explore_filled_24
    )

    @Composable
    fun Icon(selected: Boolean) {
        androidx.compose.material3.Icon(
            painter = painterResource(if (selected) iconSelected else icon),
            contentDescription = stringResource(title)
        )
    }

    companion object {
        val values = listOf(Home, AnimeList, MangaList, Profile, Explore)

        val railValues = listOf(Home, AnimeList, MangaList, Profile)

        fun String.toBottomDestinationIndex() = values.find { it.route == this }?.index

        fun Int.toBottomDestinationRoute() = values.find { it.index == this }?.route

        fun NavBackStackEntry.isBottomDestination() =
            destination.hierarchy.any { dest ->
                values.any { value -> dest.hasRoute(value.route::class) }
            }

        val BottomDestination.testTag
            get() = when (this) {
                is Home -> "HomeTab"
                is AnimeList -> "AnimeListTab"
                is MangaList -> "MangaListTab"
                is Profile -> "ProfileTab"
                is Explore -> "ExploreTab"
            }
    }
}