package com.axiel7.anihyou.ui.screens.main

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import com.axiel7.anihyou.feature.stream.ui.browse.StreamBrowseView
import com.axiel7.anihyou.feature.stream.ui.detail.StreamDetailView
import com.axiel7.anihyou.feature.stream.ui.player.PlayerView
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.axiel7.anihyou.core.common.utils.ContextUtils.openActionView
import com.axiel7.anihyou.core.model.DeepLink
import com.axiel7.anihyou.core.model.HomeTab
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.common.navigation.TopLevelBackStack
import com.axiel7.anihyou.core.ui.composables.FullScreenImageView
import com.axiel7.anihyou.core.ui.composables.markdown.MarkdownUriHandler
import com.axiel7.anihyou.core.ui.composables.markdown.SpoilerSheet
import com.axiel7.anihyou.feature.activitydetails.ActivityDetailsView
import com.axiel7.anihyou.feature.activitydetails.publish.PublishActivityView
import com.axiel7.anihyou.feature.calendar.CalendarView
import com.axiel7.anihyou.feature.characterdetails.CharacterDetailsView
import com.axiel7.anihyou.feature.explore.ExploreView
import com.axiel7.anihyou.feature.explore.charts.MediaChartListView
import com.axiel7.anihyou.feature.explore.search.SearchView
import com.axiel7.anihyou.feature.explore.season.SeasonAnimeView
import com.axiel7.anihyou.feature.home.HomeView
import com.axiel7.anihyou.feature.home.current.fulllist.CurrentFullListView
import com.axiel7.anihyou.feature.login.LoginView
import com.axiel7.anihyou.feature.mediadetails.MediaDetailsView
import com.axiel7.anihyou.feature.mediadetails.activity.MediaActivityView
import com.axiel7.anihyou.feature.notifications.NotificationsView
import com.axiel7.anihyou.feature.profile.ProfileView
import com.axiel7.anihyou.feature.reviewdetails.ReviewDetailsView
import com.axiel7.anihyou.feature.settings.SettingsView
import com.axiel7.anihyou.feature.settings.TranslationsView
import com.axiel7.anihyou.feature.settings.customlists.CustomListsView
import com.axiel7.anihyou.feature.settings.liststyle.ListStyleSettingsView
import com.axiel7.anihyou.feature.staffdetails.StaffDetailsView
import com.axiel7.anihyou.feature.studiodetails.StudioDetailsView
import com.axiel7.anihyou.feature.thread.ThreadDetailsView
import com.axiel7.anihyou.feature.thread.publish.PublishCommentView
import com.axiel7.anihyou.feature.usermedialist.UserMediaListHostView

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
    topLevelBackStack: TopLevelBackStack<NavKey>,
    navActionManager: NavActionManager,
    isCompactScreen: Boolean,
    isLoggedIn: Boolean,
    homeTab: HomeTab,
    deepLink: DeepLink?,
    padding: PaddingValues = PaddingValues(),
) {
    val context = LocalContext.current
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

    NavDisplay(
        backStack = topLevelBackStack.backStack,
        onBack = { topLevelBackStack.removeLast() },
        modifier = Modifier.padding(
            start = padding.calculateStartPadding(LocalLayoutDirection.current),
            top = padding.calculateTopPadding(),
            end = padding.calculateEndPadding(LocalLayoutDirection.current),
        ),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
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
        entryProvider = entryProvider {
            entry<Routes.Home>(
                metadata = topNavigationTransitionSpec
            ) {
                HomeView(
                    isLoggedIn = isLoggedIn,
                    defaultHomeTab = homeTab,
                    modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                    contentPadding = if (isCompactScreen) PaddingValues(bottom = 16.dp)
                    else PaddingValues(bottom = 16.dp + bottomPadding),
                    uriHandler = markdownUriHandler,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.StreamTab>(
                metadata = topNavigationTransitionSpec
            ) {
                StreamBrowseView(
                    onAnimeClick = { animeId ->
                        topLevelBackStack.add(Routes.StreamDetail(animeId))
                    },
                    modifier = Modifier.padding(bottom = bottomPadding),
                )
            }

            entry<Routes.StreamDetail>(
                metadata = topNavigationTransitionSpec
            ) { entry ->
                val args = entry.key as Routes.StreamDetail
                StreamDetailView(
                    animeId = args.animeId,
                    onBack = { topLevelBackStack.removeLastOrNull() },
                    onPlayEpisode = { animeId, provider, category, slug, epNum ->
                        topLevelBackStack.add(
                            Routes.StreamPlayer(
                                animeId = animeId,
                                provider = provider,
                                category = category,
                                episodeSlug = slug,
                                episodeNumber = epNum,
                                totalEpisodes = 0,
                            )
                        )
                    },
                )
            }

            entry<Routes.StreamPlayer>(
                metadata = topNavigationTransitionSpec
            ) { entry ->
                val args = entry.key as Routes.StreamPlayer
                PlayerView(
                    animeId = args.animeId,
                    provider = args.provider,
                    category = args.category,
                    episodeSlug = args.episodeSlug,
                    episodeNumber = args.episodeNumber,
                    totalEpisodes = args.totalEpisodes,
                    resumePositionMs = args.resumePositionMs,
                    onBack = { topLevelBackStack.removeLastOrNull() },
                    onNextEpisode = { nextEp ->
                        topLevelBackStack.removeLastOrNull()
                        topLevelBackStack.add(
                            args.copy(episodeNumber = nextEp, episodeSlug = "", resumePositionMs = 0L)
                        )
                    },
                    onPreviousEpisode = { prevEp ->
                        topLevelBackStack.removeLastOrNull()
                        topLevelBackStack.add(
                            args.copy(episodeNumber = prevEp, episodeSlug = "", resumePositionMs = 0L)
                        )
                    },
                )
            }

            entry<Routes.MangaTab>(
                metadata = topNavigationTransitionSpec
            ) {
                if (isLoggedIn) {
                    UserMediaListHostView(
                        arguments = Routes.UserMediaList(
                            mediaType = MediaType.MANGA.rawValue,
                            isCompactScreen = isCompactScreen
                        ),
                        modifier = Modifier.padding(bottom = bottomPadding),
                        navActionManager = navActionManager,
                    )
                } else {
                    LoginView()
                }
            }

            entry<Routes.Profile>(
                metadata = topNavigationTransitionSpec
            ) {
                if (isLoggedIn) {
                    ProfileView(
                        arguments = Routes.UserDetails(null, null),
                        modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                        uriHandler = markdownUriHandler,
                        navActionManager = navActionManager,
                    )
                } else {
                    LoginView(
                        showSettingsButton = true,
                        navigateToSettings = navActionManager::toSettings
                    )
                }
            }

            entry<Routes.Explore>(
                metadata = topNavigationTransitionSpec
            ) {
                ExploreView(
                    isLoggedIn = isLoggedIn,
                    modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.UserDetails> {
                ProfileView(
                    arguments = it,
                    uriHandler = markdownUriHandler,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.UserMediaList> {
                UserMediaListHostView(
                    arguments = it.copy(isCompactScreen = isCompactScreen),
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.Search> {
                SearchView(
                    arguments = it,
                    isLoggedIn = isLoggedIn,
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.Notifications> {
                if (isLoggedIn) {
                    NotificationsView(
                        arguments = it,
                        navActionManager = navActionManager,
                    )
                } else {
                    LoginView()
                }
            }

            entry<Routes.MediaDetails> {
                MediaDetailsView(
                    arguments = it.copy(isLoggedIn = isLoggedIn),
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.MediaChartList> {
                MediaChartListView(
                    arguments = it,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.SeasonAnime> {
                SeasonAnimeView(
                    arguments = it,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.Calendar> {
                CalendarView(
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.CharacterDetails> {
                CharacterDetailsView(
                    arguments = it,
                    uriHandler = markdownUriHandler,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.StaffDetails> {
                StaffDetailsView(
                    arguments = it,
                    uriHandler = markdownUriHandler,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.ReviewDetails> {
                ReviewDetailsView(
                    arguments = it,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.ThreadDetails> {
                ThreadDetailsView(
                    arguments = it,
                    uriHandler = markdownUriHandler,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.StudioDetails> {
                StudioDetailsView(
                    arguments = it,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.Settings> {
                SettingsView(
                    navActionManager = navActionManager,
                )
            }
            entry<Routes.ListStyleSettings> {
                ListStyleSettingsView(
                    navActionManager = navActionManager,
                )
            }
            entry<Routes.CustomLists> {
                CustomListsView(
                    navActionManager = navActionManager,
                )
            }
            entry<Routes.Translations> {
                TranslationsView(
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.FullScreenImage> {
                FullScreenImageView(
                    arguments = it,
                    onDismiss = navActionManager::goBack
                )
            }

            entry<Routes.ActivityDetails> {
                ActivityDetailsView(
                    arguments = it,
                    uriHandler = markdownUriHandler,
                    navActionManager = navActionManager,
                )
            }

            entry<Routes.PublishActivity> {
                if (isLoggedIn) {
                    PublishActivityView(
                        arguments = it,
                        navActionManager = navActionManager,
                    )
                } else {
                    LoginView()
                }
            }

            entry<Routes.PublishComment> {
                if (isLoggedIn) {
                    PublishCommentView(
                        arguments = it,
                        navActionManager = navActionManager,
                    )
                } else {
                    LoginView()
                }
            }

            entry<Routes.MediaActivity> {
                MediaActivityView(
                    arguments = it,
                    uriHandler = markdownUriHandler,
                    navActionManager = navActionManager
                )
            }

            entry<Routes.CurrentFullList> {
                CurrentFullListView(
                    listType = it.listType,
                    navActionManager = navActionManager,
                )
            }
        }
    )
}