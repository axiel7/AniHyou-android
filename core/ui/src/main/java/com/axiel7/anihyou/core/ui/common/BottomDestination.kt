package com.axiel7.anihyou.core.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.Route

sealed class BottomDestination(
    val index: Int,
    val route: Route,
    @param:StringRes val title: Int,
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val iconSelected: Int,
) {
    data object Home : BottomDestination(
        index = 0,
        route = Route.Home,
        title = R.string.home,
        icon = R.drawable.home_24,
        iconSelected = R.drawable.home_filled_24
    )

    data object AnimeList : BottomDestination(
        index = 1,
        route = Route.AnimeTab,
        title = R.string.anime,
        icon = R.drawable.live_tv_24,
        iconSelected = R.drawable.live_tv_filled_24
    )

    data object MangaList : BottomDestination(
        index = 2,
        route = Route.MangaTab,
        title = R.string.manga,
        icon = R.drawable.book_24,
        iconSelected = R.drawable.book_filled_24
    )

    data object Profile : BottomDestination(
        index = 3,
        route = Route.Profile,
        title = R.string.profile,
        icon = R.drawable.person_24,
        iconSelected = R.drawable.person_filled_24
    )

    data object Explore : BottomDestination(
        index = 4,
        route = Route.Explore,
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
        val routes = setOf(Home.route, AnimeList.route, MangaList.route, Profile.route, Explore.route)

        val values = listOf(Home, AnimeList, MangaList, Profile, Explore)

        val railValues = listOf(Home, AnimeList, MangaList, Profile)

        fun Int.toBottomDestinationRoute(): NavKey? = values.find { it.index == this }?.route

        fun NavKey.isBottomDestination() = values.any { it.route == this }

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