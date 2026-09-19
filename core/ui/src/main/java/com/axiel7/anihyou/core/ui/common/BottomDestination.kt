package com.axiel7.anihyou.core.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.Route

sealed class BottomDestination(
    val index: Int,
    val route: Route,
    @param:StringRes val title: Int,
    @param:DrawableRes val icon: Int,
) {
    data object Home : BottomDestination(
        index = 0,
        route = Route.Home,
        title = R.string.home,
        icon = R.drawable.anim_home,
    )

    data object AnimeList : BottomDestination(
        index = 1,
        route = Route.AnimeTab,
        title = R.string.anime,
        icon = R.drawable.anim_tv,
    )

    data object MangaList : BottomDestination(
        index = 2,
        route = Route.MangaTab,
        title = R.string.manga,
        icon = R.drawable.anim_book,
    )

    data object Profile : BottomDestination(
        index = 3,
        route = Route.Profile,
        title = R.string.profile,
        icon = R.drawable.anim_person,
    )

    data object Explore : BottomDestination(
        index = 4,
        route = Route.Explore,
        title = R.string.explore,
        icon = R.drawable.anim_explore,
    )

    companion object {
        val routes = setOf(Home.route, AnimeList.route, MangaList.route, Profile.route, Explore.route)

        val values = listOf(Home, AnimeList, MangaList, Profile, Explore)

        val railValues = listOf(Home, AnimeList, MangaList, Profile)

        fun Int.toBottomDestinationRoute(): Route? = values.find { it.index == this }?.route

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