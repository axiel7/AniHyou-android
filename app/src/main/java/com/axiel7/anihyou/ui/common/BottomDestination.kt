package com.axiel7.anihyou.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.screens.usermedialist.UserMediaList

sealed class BottomDestination(
    val index: Int,
    val route: Any,
    val routeName: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int,
) {
    data object Home : BottomDestination(
        index = 0,
        route = com.axiel7.anihyou.ui.screens.home.Home,
        routeName = "Home",
        title = R.string.home,
        icon = R.drawable.home_24,
        iconSelected = R.drawable.home_filled_24
    )

    data object AnimeList : BottomDestination(
        index = 1,
        route = UserMediaList(mediaType = MediaType.ANIME.rawValue),
        routeName = "UserAnimeList",
        title = R.string.anime,
        icon = R.drawable.live_tv_24,
        iconSelected = R.drawable.live_tv_filled_24
    )

    data object MangaList : BottomDestination(
        index = 2,
        route = UserMediaList(mediaType = MediaType.MANGA.rawValue),
        routeName = "UserMangaList",
        title = R.string.manga,
        icon = R.drawable.book_24,
        iconSelected = R.drawable.book_filled_24
    )

    data object Profile : BottomDestination(
        index = 3,
        route = com.axiel7.anihyou.ui.screens.profile.Profile(id = 0, userName = null),
        routeName = "Profile",
        title = R.string.profile,
        icon = R.drawable.person_24,
        iconSelected = R.drawable.person_filled_24
    )

    data object Explore : BottomDestination(
        index = 4,
        route = com.axiel7.anihyou.ui.screens.explore.Explore,
        routeName = "Explore",
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

        fun NavBackStackEntry.toBottomDestination() =
            destination.route?.split(".")?.last()?.let { routeName ->
                val bottomDestination = values.find { routeName.startsWith(it.routeName) }
                when (bottomDestination) {
                    AnimeList, MangaList -> {
                        val mediaType =
                            arguments?.getString("mediaType")?.let { MediaType.safeValueOf(it) }
                        when (mediaType) {
                            MediaType.ANIME -> AnimeList
                            MediaType.MANGA -> MangaList
                            else -> bottomDestination
                        }
                    }

                    else -> bottomDestination
                }
            }
    }
}