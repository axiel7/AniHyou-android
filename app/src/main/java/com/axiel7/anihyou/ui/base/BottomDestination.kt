package com.axiel7.anihyou.ui.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.axiel7.anihyou.R

sealed class BottomDestination(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int,
) {
    object Home: BottomDestination(
        route = "home",
        title = R.string.home,
        icon = R.drawable.home_24,
        iconSelected = R.drawable.home_filled_24
    )
    object AnimeList: BottomDestination(
        route = "anime_list",
        title = R.string.anime,
        icon = R.drawable.live_tv_24,
        iconSelected = R.drawable.live_tv_filled_24
    )
    object MangaList: BottomDestination(
        route = "manga_list",
        title = R.string.manga,
        icon = R.drawable.book_24,
        iconSelected = R.drawable.book_filled_24
    )
    object Profile: BottomDestination(
        route = "profile",
        title = R.string.profile,
        icon = R.drawable.person_24,
        iconSelected = R.drawable.person_filled_24
    )
    object Explore: BottomDestination(
        route = "explore",
        title = R.string.explore,
        icon = R.drawable.explore_24,
        iconSelected = R.drawable.explore_filled_24
    )

    companion object {
        fun String.toBottomDestinationIndex() = when (this) {
            Home.route -> 0
            AnimeList.route -> 1
            MangaList.route -> 2
            Profile.route -> 3
            Explore.route -> 4
            else -> null
        }
    }
}