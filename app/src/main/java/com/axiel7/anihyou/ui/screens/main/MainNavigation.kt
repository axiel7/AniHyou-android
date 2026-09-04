package com.axiel7.anihyou.ui.screens.main

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.axiel7.anihyou.core.common.utils.ContextUtils.openActionView
import com.axiel7.anihyou.core.model.DeepLink
import com.axiel7.anihyou.core.model.ExploreTab
import com.axiel7.anihyou.core.model.HomeTab
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.ui.common.LocalMarkdownUriHandler
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Navigator
import com.axiel7.anihyou.core.ui.common.navigation.Route
import com.axiel7.anihyou.core.ui.composables.FullScreenImageView
import com.axiel7.anihyou.core.ui.composables.markdown.MarkdownUriHandler
import com.axiel7.anihyou.core.ui.composables.markdown.SpoilerSheet
import com.axiel7.anihyou.feature.activitydetails.ActivityDetailsView
import com.axiel7.anihyou.feature.activitydetails.publish.PublishActivityView
import com.axiel7.anihyou.feature.calendar.CalendarView
import com.axiel7.anihyou.feature.characterdetails.CharacterDetailsView
import com.axiel7.anihyou.feature.explore.charts.MediaChartListView
import com.axiel7.anihyou.feature.explore.discover.ExploreView
import com.axiel7.anihyou.feature.explore.search.SearchView
import com.axiel7.anihyou.feature.explore.season.SeasonAnimeView
import com.axiel7.anihyou.feature.home.HomeView
import com.axiel7.anihyou.feature.home.current.fulllist.CurrentFullListView
import com.axiel7.anihyou.feature.login.LoginView
import com.axiel7.anihyou.feature.mediadetails.MediaDetailsView
import com.axiel7.anihyou.feature.mediadetails.activity.MediaActivityView
import com.axiel7.anihyou.feature.mediadetails.characters.MediaCharactersView
import com.axiel7.anihyou.feature.notifications.NotificationsView
import com.axiel7.anihyou.feature.profile.ProfileView
import com.axiel7.anihyou.feature.profile.favorites.reorder.ReorderFavoritesView
import com.axiel7.anihyou.feature.reviewdetails.ReviewDetailsView
import com.axiel7.anihyou.feature.settings.ContributorsView
import com.axiel7.anihyou.feature.settings.SettingsView
import com.axiel7.anihyou.feature.settings.TranslationsView
import com.axiel7.anihyou.feature.settings.customlists.CustomListsView
import com.axiel7.anihyou.feature.settings.liststyle.ListStyleSettingsView
import com.axiel7.anihyou.feature.settings.priority_colors.PriorityColorView
import com.axiel7.anihyou.feature.staffdetails.StaffDetailsView
import com.axiel7.anihyou.feature.studiodetails.StudioDetailsView
import com.axiel7.anihyou.feature.thread.ThreadDetailsView
import com.axiel7.anihyou.feature.thread.comment.ThreadCommentDetailsView
import com.axiel7.anihyou.feature.thread.publish.PublishCommentView
import com.axiel7.anihyou.feature.usermedialist.UserMediaListHostView
import com.materialkolor.PaletteStyle

private val topNavigationTransitionSpec = NavDisplay.transitionSpec {
    ContentTransform(
        fadeIn(animationSpec = tween()),
        fadeOut(animationSpec = tween()),
    )
} + NavDisplay.popTransitionSpec {
    ContentTransform(
        fadeIn(animationSpec = tween()),
        fadeOut(animationSpec = tween()),
    )
} + NavDisplay.predictivePopTransitionSpec {
    ContentTransform(
        fadeIn(spring(dampingRatio = 1f, stiffness = 1600f)),
        fadeOut(spring(dampingRatio = 1f, stiffness = 1600f))
    )
}

@Composable
fun MainNavigation(
    navigator: Navigator,
    isCompactScreen: Boolean,
    isLoggedIn: Boolean,
    homeTab: HomeTab,
    exploreTab: ExploreTab,
    deepLink: DeepLink?,
    blackColors: Boolean,
    paletteStyle: PaletteStyle,
    padding: PaddingValues = PaddingValues(),
) {
    val context = LocalContext.current
    val navActionManager = LocalNavActionManager.current
    val bottomPadding by animateDpAsState(
        targetValue = padding.calculateBottomPadding(),
        label = "bottom_bar_padding"
    )

    var spoilerText by remember { mutableStateOf<String?>(null) }
    val markdownUriHandler = remember {
        MarkdownUriHandler(
            onSpoilerClicked = { spoilerText = it },
            onLinkClicked = { context.openActionView(it) },
        )
    }

    spoilerText?.let {
        SpoilerSheet(
            text = it,
            uriHandler = markdownUriHandler,
            onDismiss = { spoilerText = null }
        )
    }


    LaunchedEffect(deepLink) {
        if (deepLink != null) {
            when (deepLink.type) {
                DeepLink.Type.ANIME, DeepLink.Type.MANGA -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toMediaDetails(it) }
                }

                DeepLink.Type.USER -> {
                    navActionManager.toUserDetails(
                        userId = deepLink.id.toIntOrNull(),
                        username = deepLink.id
                    )
                }

                DeepLink.Type.SEARCH -> navActionManager.toSearch()

                DeepLink.Type.CHARACTER -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toCharacterDetails(it) }
                }

                DeepLink.Type.STAFF -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toStaffDetails(it) }
                }

                DeepLink.Type.STUDIO -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toStudioDetails(it) }
                }

                DeepLink.Type.THREAD -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toThreadDetails(it) }
                }

                DeepLink.Type.ACTIVITY -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toActivityDetails(it) }
                }
            }
        }
    }

    val entryProvider = entryProvider<NavKey> {
        entry<Route.Home>(
            metadata = topNavigationTransitionSpec
        ) {
            HomeView(
                isLoggedIn = isLoggedIn,
                defaultHomeTab = homeTab,
                modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
            )
        }

        entry<Route.AnimeTab>(
            metadata = topNavigationTransitionSpec
        ) {
            if (isLoggedIn) {
                UserMediaListHostView(
                    arguments = Route.UserMediaList(
                        mediaType = MediaType.ANIME.rawValue,
                    ),
                    isCompactScreen = isCompactScreen,
                    modifier = Modifier.padding(bottom = bottomPadding),
                )
            } else {
                LoginView()
            }
        }

        entry<Route.MangaTab>(
            metadata = topNavigationTransitionSpec
        ) {
            if (isLoggedIn) {
                UserMediaListHostView(
                    arguments = Route.UserMediaList(
                        mediaType = MediaType.MANGA.rawValue,
                    ),
                    isCompactScreen = isCompactScreen,
                    modifier = Modifier.padding(bottom = bottomPadding),
                )
            } else {
                LoginView()
            }
        }

        entry<Route.Profile>(
            metadata = topNavigationTransitionSpec
        ) {
            if (isLoggedIn) {
                ProfileView(
                    arguments = Route.UserDetails(null, null),
                    modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                )
            } else {
                LoginView(
                    showSettingsButton = true,
                    navigateToSettings = navActionManager::toSettings
                )
            }
        }

        entry<Route.Explore>(
            metadata = topNavigationTransitionSpec
        ) {
            ExploreView(
                defaultExploreTab = exploreTab,
                isLoggedIn = isLoggedIn,
                contentPadding = if (isCompactScreen) PaddingValues(bottom = bottomPadding) else PaddingValues(),
            )
        }

        entry<Route.UserDetails> {
            ProfileView(
                arguments = it,
            )
        }

        entry<Route.UserMediaList> {
            UserMediaListHostView(
                arguments = it,
                isCompactScreen = isCompactScreen,
                modifier = Modifier.padding(bottom = bottomPadding),
            )
        }

        entry<Route.Search>(
            metadata = NavDisplay.transitionSpec {
                (slideInVertically { -it } + fadeIn()) togetherWith fadeOut()
            } + NavDisplay.popTransitionSpec {
                fadeIn() togetherWith (slideOutVertically { -it } + fadeOut())
            } + NavDisplay.predictivePopTransitionSpec {
                fadeIn() togetherWith (slideOutVertically { -it } + fadeOut())
            }
        ) {
            SearchView(
                arguments = it,
                isLoggedIn = isLoggedIn,
                modifier = Modifier.padding(bottom = bottomPadding),
            )
        }

        entry<Route.Notifications> {
            if (isLoggedIn) {
                NotificationsView(
                    arguments = it,
                )
            } else {
                LoginView()
            }
        }

        entry<Route.MediaDetails> {
            MediaDetailsView(
                arguments = it.copy(isLoggedIn = isLoggedIn),
                blackColors = blackColors,
                paletteStyle = paletteStyle,
            )
        }

        entry<Route.MediaChartList> {
            MediaChartListView(
                arguments = it,
                isLoggedIn = isLoggedIn,
            )
        }

        entry<Route.SeasonAnime> {
            SeasonAnimeView(
                isLoggedIn = isLoggedIn,
                arguments = it,
            )
        }

        entry<Route.Calendar> {
            CalendarView(
                isLoggedIn = isLoggedIn,
            )
        }

        entry<Route.CharacterDetails> {
            CharacterDetailsView(
                isLoggedIn = isLoggedIn,
                arguments = it,
            )
        }

        entry<Route.StaffDetails> {
            StaffDetailsView(
                isLoggedIn = isLoggedIn,
                arguments = it,
            )
        }

        entry<Route.ReviewDetails> {
            ReviewDetailsView(
                arguments = it,
            )
        }

        entry<Route.ThreadDetails> {
            ThreadDetailsView(
                arguments = it,
            )
        }

        entry<Route.ThreadCommentDetails> {
            ThreadCommentDetailsView(
                arguments = it,
            )
        }

        entry<Route.StudioDetails> {
            StudioDetailsView(
                arguments = it,
            )
        }

        entry<Route.Settings> {
            SettingsView()
        }
        entry<Route.ListStyleSettings> {
            ListStyleSettingsView()
        }
        entry<Route.CustomLists> {
            CustomListsView()
        }
        entry<Route.Translations> {
            TranslationsView()
        }
        entry<Route.Contributors> {
            ContributorsView()
        }

        entry<Route.FullScreenImage> {
            FullScreenImageView(
                arguments = it,
                isCompactScreen = isCompactScreen,
                onDismiss = navActionManager::goBack
            )
        }

        entry<Route.ActivityDetails> {
            ActivityDetailsView(
                arguments = it,
            )
        }

        entry<Route.PublishActivity> {
            if (isLoggedIn) {
                PublishActivityView(
                    arguments = it,
                )
            } else {
                LoginView()
            }
        }

        entry<Route.PublishComment> {
            if (isLoggedIn) {
                PublishCommentView(
                    arguments = it,
                )
            } else {
                LoginView()
            }
        }

        entry<Route.MediaActivity> {
            MediaActivityView(
                arguments = it,
            )
        }

        entry<Route.MediaCharacters> {
            MediaCharactersView(
                arguments = it,
            )
        }

        entry<Route.CurrentFullList> {
            CurrentFullListView(
                isLoggedIn = isLoggedIn,
                listType = it.listType,
            )
        }

        entry<Route.ReorderFavorites> {
            ReorderFavoritesView(
                arguments = it,
            )
        }

        entry<Route.PriorityColors> {
            PriorityColorView()
        }
    }

    CompositionLocalProvider(LocalMarkdownUriHandler provides markdownUriHandler) {
        NavDisplay(
            entries = navigator.state.toDecoratedEntries(entryProvider),
            modifier = Modifier.padding(
                start = padding.calculateStartPadding(LocalLayoutDirection.current),
                top = padding.calculateTopPadding(),
                end = padding.calculateEndPadding(LocalLayoutDirection.current),
            ),
            transitionSpec = {
                // Slide in from right when navigating forward
                (slideInHorizontally(initialOffsetX = { it })) togetherWith
                        (slideOutHorizontally(targetOffsetX = { -it })
                                + fadeOut(animationSpec = tween()))
            },
            popTransitionSpec = {
                // Slide in from left when navigating back
                (slideInHorizontally(initialOffsetX = { -it }) + fadeIn()) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                // Slide in from left when navigating back
                (slideInHorizontally(initialOffsetX = { -it })
                        + fadeIn(animationSpec = tween())) togetherWith
                        (slideOutHorizontally(targetOffsetX = { it }))
            },
            onBack = navigator::goBack,
        )
    }
}
