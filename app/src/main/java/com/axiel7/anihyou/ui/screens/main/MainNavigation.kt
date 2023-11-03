package com.axiel7.anihyou.ui.screens.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.axiel7.anihyou.data.model.DeepLink
import com.axiel7.anihyou.ui.common.BottomDestination
import com.axiel7.anihyou.ui.common.BottomDestination.Companion.toBottomDestinationRoute
import com.axiel7.anihyou.ui.common.DestArgument.Companion.getBoolean
import com.axiel7.anihyou.ui.common.DestArgument.Companion.getIntArg
import com.axiel7.anihyou.ui.common.DestArgument.Companion.getStringArg
import com.axiel7.anihyou.ui.common.NavArgument
import com.axiel7.anihyou.ui.common.NavDestination
import com.axiel7.anihyou.ui.composables.FullScreenImageView
import com.axiel7.anihyou.ui.screens.activitydetails.ActivityDetailsView
import com.axiel7.anihyou.ui.screens.activitydetails.publish.PublishActivityView
import com.axiel7.anihyou.ui.screens.calendar.CalendarView
import com.axiel7.anihyou.ui.screens.characterdetails.CharacterDetailsView
import com.axiel7.anihyou.ui.screens.explore.ExploreView
import com.axiel7.anihyou.ui.screens.explore.charts.MediaChartListView
import com.axiel7.anihyou.ui.screens.explore.search.SearchView
import com.axiel7.anihyou.ui.screens.explore.season.SeasonAnimeView
import com.axiel7.anihyou.ui.screens.home.HomeTab
import com.axiel7.anihyou.ui.screens.home.HomeView
import com.axiel7.anihyou.ui.screens.login.LoginView
import com.axiel7.anihyou.ui.screens.mediadetails.MediaDetailsView
import com.axiel7.anihyou.ui.screens.notifications.NotificationsView
import com.axiel7.anihyou.ui.screens.profile.ProfileView
import com.axiel7.anihyou.ui.screens.reviewdetails.ReviewDetailsView
import com.axiel7.anihyou.ui.screens.settings.SettingsView
import com.axiel7.anihyou.ui.screens.settings.TranslationsView
import com.axiel7.anihyou.ui.screens.settings.liststyle.ListStyleSettingsView
import com.axiel7.anihyou.ui.screens.staffdetails.StaffDetailsView
import com.axiel7.anihyou.ui.screens.studiodetails.StudioDetailsView
import com.axiel7.anihyou.ui.screens.thread.ThreadDetailsView
import com.axiel7.anihyou.ui.screens.thread.publish.PublishCommentView
import com.axiel7.anihyou.ui.screens.usermedialist.UserMediaListHostView
import com.axiel7.anihyou.utils.NumberUtils.toStringOrZero
import com.axiel7.anihyou.utils.UTF_8
import java.net.URLEncoder

@Composable
fun MainNavigation(
    navController: NavHostController,
    isCompactScreen: Boolean,
    isLoggedIn: Boolean,
    lastTabOpened: Int,
    homeTab: HomeTab,
    deepLink: DeepLink?,
    padding: PaddingValues = PaddingValues(),
) {
    val bottomPadding by animateDpAsState(
        targetValue = padding.calculateBottomPadding(),
        label = "bottom_bar_padding"
    )

    // common navigation actions
    val navigateBack: () -> Unit = { navController.popBackStack() }
    val navigateToMediaDetails: (Int) -> Unit = { id ->
        navController.navigate(
            NavDestination.MediaDetails
                .putArguments(mapOf(NavArgument.MediaId to id.toString()))
        )
    }
    val navigateToCharacterDetails: (Int) -> Unit = { id ->
        navController.navigate(
            NavDestination.CharacterDetails
                .putArguments(mapOf(NavArgument.CharacterId to id.toString()))
        )
    }
    val navigateToStaffDetails: (Int) -> Unit = { id ->
        navController.navigate(
            NavDestination.StaffDetails
                .putArguments(mapOf(NavArgument.StaffId to id.toString()))
        )
    }
    val navigateToStudioDetails: (Int) -> Unit = { id ->
        navController.navigate(
            NavDestination.StudioDetails
                .putArguments(mapOf(NavArgument.StudioId to id.toString()))
        )
    }
    val navigateToUserDetails: (Int) -> Unit = { id ->
        navController.navigate(
            NavDestination.UserDetails
                .putArguments(mapOf(NavArgument.UserId to id.toString()))
        )
    }
    val navigateToActivityDetails: (Int) -> Unit = { id ->
        navController.navigate(
            NavDestination.ActivityDetails
                .putArguments(mapOf(NavArgument.ActivityId to id.toString()))
        )
    }
    val navigateToFullscreenImage: (String) -> Unit = { url ->
        val encodedUrl = URLEncoder.encode(url, UTF_8)
        navController.navigate(
            NavDestination.FullscreenImage
                .putArguments(mapOf(NavArgument.Url to encodedUrl))
        )
    }

    LaunchedEffect(deepLink) {
        if (deepLink != null) {
            when (deepLink.type) {
                DeepLink.Type.ANIME, DeepLink.Type.MANGA -> {
                    navigateToMediaDetails(deepLink.id.toInt())
                }

                DeepLink.Type.USER -> {
                    val userId = deepLink.id.toIntOrNull()
                    navController.navigate(
                        NavDestination.UserDetails.putArguments(
                            mapOf(
                                NavArgument.UserId to (userId ?: 0).toString(),
                                NavArgument.UserName to deepLink.id
                            )
                        )
                    )
                }

                DeepLink.Type.SEARCH -> navController.navigate(NavDestination.Search.route())

                DeepLink.Type.CHARACTER -> navigateToCharacterDetails(deepLink.id.toInt())

                DeepLink.Type.STAFF -> navigateToStaffDetails(deepLink.id.toInt())

                DeepLink.Type.STUDIO -> navigateToStudioDetails(deepLink.id.toInt())
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = lastTabOpened.toBottomDestinationRoute()
            ?: NavDestination.HomeTab.route(),
        modifier = Modifier.padding(
            start = padding.calculateStartPadding(LocalLayoutDirection.current),
            top = padding.calculateTopPadding(),
            end = padding.calculateEndPadding(LocalLayoutDirection.current),
        ),
        enterTransition = {
            fadeIn(tween(400))
        },
        exitTransition = {
            fadeOut(tween(400))
        },
        popEnterTransition = {
            fadeIn(tween(400))
        },
        popExitTransition = {
            fadeOut(tween(400))
        }
    ) {
        composable(BottomDestination.Home.route) {
            HomeView(
                isLoggedIn = isLoggedIn,
                defaultHomeTab = homeTab,
                modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                contentPadding = if (isCompactScreen) PaddingValues(bottom = 16.dp)
                else PaddingValues(bottom = 16.dp + bottomPadding),
                navigateToMediaDetails = navigateToMediaDetails,
                navigateToAnimeSeason = { animeSeason ->
                    navController.navigate(
                        NavDestination.SeasonAnime.putArguments(
                            mapOf(
                                NavArgument.Year to animeSeason.year.toString(),
                                NavArgument.Season to animeSeason.season.name
                            )
                        )
                    )
                },
                navigateToCalendar = {
                    navController.navigate(NavDestination.Calendar.route())
                },
                navigateToExplore = { mediaType, mediaSort ->
                    navController.navigate(
                        NavDestination.Search.putArguments(
                            mapOf(
                                NavArgument.MediaType to mediaType.rawValue,
                                NavArgument.MediaSort to mediaSort.rawValue
                            )
                        )
                    )
                },
                navigateToNotifications = { unread ->
                    navController.navigate(
                        NavDestination.Notifications
                            .putArguments(mapOf(NavArgument.UnreadCount to unread.toString()))
                    )
                },
                navigateToUserDetails = navigateToUserDetails,
                navigateToActivityDetails = navigateToActivityDetails,
                navigateToPublishActivity = { id, text ->
                    navController.navigate(
                        NavDestination.PublishActivity.putArguments(
                            mapOf(
                                NavArgument.ActivityId to id.toStringOrZero(),
                                NavArgument.Text to text
                            )
                        )
                    )
                },
                navigateToFullscreenImage = navigateToFullscreenImage,
            )
        }

        composable(
            route = BottomDestination.AnimeList.route,
            arguments = NavDestination.AnimeTab.namedNavArguments
        ) {
            if (isLoggedIn) {
                UserMediaListHostView(
                    isCompactScreen = isCompactScreen,
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navigateToMediaDetails = navigateToMediaDetails,
                    navigateToSearch = { mediaType ->
                        navController.navigate(
                            NavDestination.Search.putArguments(
                                mapOf(
                                    NavArgument.MediaType to mediaType.rawValue,
                                    NavArgument.OnList to true.toString(),
                                    NavArgument.Focus to true.toString()
                                )
                            )
                        )
                    }
                )
            } else {
                LoginView()
            }
        }

        composable(
            route = BottomDestination.MangaList.route,
            arguments = NavDestination.MangaTab.namedNavArguments
        ) {
            if (isLoggedIn) {
                UserMediaListHostView(
                    isCompactScreen = isCompactScreen,
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navigateToMediaDetails = navigateToMediaDetails,
                    navigateToSearch = { mediaType ->
                        navController.navigate(
                            NavDestination.Search.putArguments(
                                mapOf(
                                    NavArgument.MediaType to mediaType.rawValue,
                                    NavArgument.OnList to true.toString(),
                                    NavArgument.Focus to true.toString()
                                )
                            )
                        )
                    }
                )
            } else {
                LoginView()
            }
        }

        composable(BottomDestination.Profile.route) {
            if (isLoggedIn) {
                ProfileView(
                    modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                    navigateToSettings = {
                        navController.navigate(NavDestination.Settings.route())
                    },
                    navigateToFullscreenImage = navigateToFullscreenImage,
                    navigateToMediaDetails = navigateToMediaDetails,
                    navigateToCharacterDetails = navigateToCharacterDetails,
                    navigateToStaffDetails = navigateToStaffDetails,
                    navigateToStudioDetails = navigateToStudioDetails,
                    navigateToUserDetails = navigateToUserDetails,
                    navigateToActivityDetails = navigateToActivityDetails,
                    navigateToUserMediaList = null
                )
            } else {
                LoginView()
            }
        }

        composable(BottomDestination.Explore.route) {
            ExploreView(
                modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                navigateToMediaDetails = navigateToMediaDetails,
                navigateToUserDetails = navigateToUserDetails,
                navigateToCharacterDetails = navigateToCharacterDetails,
                navigateToStaffDetails = navigateToStaffDetails,
                navigateToStudioDetails = navigateToStudioDetails,
                navigateToMediaChart = { type ->
                    navController.navigate(
                        NavDestination.MediaChart.putArguments(
                            mapOf(NavArgument.ChartType to type.name)
                        )
                    )
                },
                navigateToAnimeSeason = { year, season ->
                    navController.navigate(
                        NavDestination.SeasonAnime.putArguments(
                            mapOf(
                                NavArgument.Year to year.toString(),
                                NavArgument.Season to season
                            )
                        )
                    )
                },
                navigateToCalendar = {
                    navController.navigate(NavDestination.Calendar.route())
                }
            )
        }

        composable(
            route = NavDestination.Search.route(),
            arguments = NavDestination.Search.namedNavArguments
        ) { navEntry ->
            SearchView(
                modifier = Modifier.padding(bottom = bottomPadding),
                initialGenre = navEntry.getStringArg(
                    NavDestination.Search.findDestArgument(NavArgument.Genre)
                ),
                initialTag = navEntry.getStringArg(
                    NavDestination.Search.findDestArgument(NavArgument.Tag)
                ),
                initialFocus = navEntry.getBoolean(
                    NavDestination.Search.findDestArgument(NavArgument.Focus)
                ) == true,
                navigateBack = navigateBack,
                navigateToMediaDetails = navigateToMediaDetails,
                navigateToUserDetails = navigateToUserDetails,
                navigateToCharacterDetails = navigateToCharacterDetails,
                navigateToStaffDetails = navigateToStaffDetails,
                navigateToStudioDetails = navigateToStudioDetails,
            )
        }

        composable(
            route = NavDestination.UserMediaList.route(),
            arguments = NavDestination.UserMediaList.namedNavArguments
        ) {
            UserMediaListHostView(
                isCompactScreen = isCompactScreen,
                modifier = Modifier.padding(bottom = bottomPadding),
                navigateToMediaDetails = navigateToMediaDetails,
                navigateBack = navigateBack
            )
        }

        composable(
            route = NavDestination.Notifications.route(),
            arguments = NavDestination.Notifications.namedNavArguments
        ) {
            if (isLoggedIn) {
                NotificationsView(
                    navigateToMediaDetails = navigateToMediaDetails,
                    navigateToUserDetails = navigateToUserDetails,
                    navigateToActivityDetails = navigateToActivityDetails,
                    navigateToThreadDetails = { id ->
                        navController.navigate(
                            NavDestination.ThreadDetails.putArguments(
                                mapOf(NavArgument.ThreadId to id.toString())
                            )
                        )
                    },
                    navigateBack = navigateBack
                )
            } else {
                LoginView()
            }
        }

        composable(
            route = NavDestination.MediaDetails.route(),
            arguments = NavDestination.MediaDetails.namedNavArguments,
        ) {
            MediaDetailsView(
                isLoggedIn = isLoggedIn,
                navigateBack = navigateBack,
                navigateToMediaDetails = navigateToMediaDetails,
                navigateToFullscreenImage = navigateToFullscreenImage,
                navigateToStudioDetails = navigateToStudioDetails,
                navigateToCharacterDetails = navigateToCharacterDetails,
                navigateToStaffDetails = navigateToStaffDetails,
                navigateToReviewDetails = { id ->
                    navController.navigate(
                        NavDestination.ReviewDetails.putArguments(
                            mapOf(NavArgument.ReviewId to id.toString())
                        )
                    )
                },
                navigateToThreadDetails = { id ->
                    navController.navigate(
                        NavDestination.ThreadDetails.putArguments(
                            mapOf(NavArgument.ThreadId to id.toString())
                        )
                    )
                },
                navigateToExplore = { mediaType, genre, tag ->
                    navController.navigate(
                        NavDestination.Search.putArguments(
                            mapOf(
                                NavArgument.MediaType to mediaType?.rawValue,
                                NavArgument.Genre to genre,
                                NavArgument.Tag to tag
                            )
                        )
                    )
                }
            )
        }

        composable(
            route = NavDestination.MediaChart.route(),
            arguments = NavDestination.MediaChart.namedNavArguments
        ) {
            MediaChartListView(
                navigateBack = navigateBack,
                navigateToMediaDetails = navigateToMediaDetails
            )
        }

        composable(
            NavDestination.SeasonAnime.route(),
            arguments = NavDestination.SeasonAnime.namedNavArguments
        ) {
            SeasonAnimeView(
                navigateBack = navigateBack,
                navigateToMediaDetails = navigateToMediaDetails
            )
        }

        composable(NavDestination.Calendar.route()) {
            CalendarView(
                navigateToMediaDetails = navigateToMediaDetails,
                navigateBack = navigateBack
            )
        }

        composable(
            route = NavDestination.UserDetails.route(),
            arguments = NavDestination.UserDetails.namedNavArguments
        ) {
            ProfileView(
                modifier = Modifier.padding(bottom = bottomPadding),
                navigateToFullscreenImage = navigateToFullscreenImage,
                navigateToMediaDetails = navigateToMediaDetails,
                navigateToCharacterDetails = navigateToCharacterDetails,
                navigateToStaffDetails = navigateToStaffDetails,
                navigateToStudioDetails = navigateToStudioDetails,
                navigateToUserDetails = navigateToUserDetails,
                navigateToActivityDetails = navigateToActivityDetails,
                navigateToUserMediaList = { mediaType, userId, scoreFormat ->
                    navController.navigate(
                        NavDestination.UserMediaList.putArguments(
                            mapOf(
                                NavArgument.UserId to userId.toString(),
                                NavArgument.MediaType to mediaType.rawValue,
                                NavArgument.ScoreFormat to scoreFormat.rawValue
                            )
                        )
                    )
                },
                navigateBack = navigateBack,
            )
        }

        composable(
            route = NavDestination.CharacterDetails.route(),
            arguments = NavDestination.CharacterDetails.namedNavArguments
        ) {
            CharacterDetailsView(
                navigateBack = navigateBack,
                navigateToMediaDetails = navigateToMediaDetails,
                navigateToFullscreenImage = navigateToFullscreenImage,
            )
        }

        composable(
            route = NavDestination.StaffDetails.route(),
            arguments = NavDestination.StaffDetails.namedNavArguments
        ) {
            StaffDetailsView(
                navigateBack = navigateBack,
                navigateToMediaDetails = navigateToMediaDetails,
                navigateToCharacterDetails = navigateToCharacterDetails,
                navigateToFullscreenImage = navigateToFullscreenImage
            )
        }

        composable(
            route = NavDestination.ReviewDetails.route(),
            arguments = NavDestination.ReviewDetails.namedNavArguments
        ) {
            ReviewDetailsView(
                navigateBack = navigateBack
            )
        }

        composable(
            route = NavDestination.ThreadDetails.route(),
            arguments = NavDestination.ThreadDetails.namedNavArguments
        ) { navEntry ->
            navEntry.getIntArg(
                NavDestination.ThreadDetails.findDestArgument(NavArgument.ThreadId)
            )?.let { threadId ->
                ThreadDetailsView(
                    navigateToUserDetails = navigateToUserDetails,
                    navigateToPublishThreadComment = { commentId, text ->
                        navController.navigate(
                            NavDestination.PublishThreadComment.putArguments(
                                mapOf(
                                    NavArgument.ThreadId to threadId.toString(),
                                    NavArgument.CommentId to commentId.toStringOrZero(),
                                    NavArgument.Text to text
                                )
                            )
                        )
                    },
                    navigateToPublishCommentReply = { parentCommentId, commentId, text ->
                        navController.navigate(
                            NavDestination.PublishCommentReply.putArguments(
                                mapOf(
                                    NavArgument.ParentCommentId to parentCommentId.toString(),
                                    NavArgument.CommentId to commentId.toStringOrZero(),
                                    NavArgument.Text to text
                                )
                            )
                        )
                    },
                    navigateToFullscreenImage = navigateToFullscreenImage,
                    navigateBack = navigateBack
                )
            }
        }

        composable(
            route = NavDestination.StudioDetails.route(),
            arguments = NavDestination.StudioDetails.namedNavArguments
        ) {
            StudioDetailsView(
                navigateBack = navigateBack,
                navigateToMediaDetails = navigateToMediaDetails
            )
        }

        composable(NavDestination.Settings.route()) {
            SettingsView(
                navigateToListStyleSettings = {
                    navController.navigate(NavDestination.ListStyleSettings.route())
                },
                navigateToTranslations = {
                    navController.navigate(NavDestination.Translations.route())
                },
                navigateBack = navigateBack
            )
        }
        composable(NavDestination.ListStyleSettings.route()) {
            ListStyleSettingsView(
                navigateBack = navigateBack
            )
        }
        composable(NavDestination.Translations.route()) {
            TranslationsView(
                navigateBack = navigateBack
            )
        }

        composable(
            route = NavDestination.FullscreenImage.route(),
            arguments = NavDestination.FullscreenImage.namedNavArguments
        ) { navEntry ->
            FullScreenImageView(
                imageUrl = navEntry.getStringArg(
                    NavDestination.FullscreenImage.findDestArgument(NavArgument.Url)
                ),
                onDismiss = navigateBack
            )
        }

        composable(
            route = NavDestination.ActivityDetails.route(),
            arguments = NavDestination.ActivityDetails.namedNavArguments
        ) { navEntry ->
            navEntry.getIntArg(
                NavDestination.ActivityDetails.findDestArgument(NavArgument.ActivityId)
            )?.let { activityId ->
                ActivityDetailsView(
                    navigateBack = navigateBack,
                    navigateToUserDetails = navigateToUserDetails,
                    navigateToPublishActivityReply = { id, text ->
                        navController.navigate(
                            NavDestination.PublishActivityReply.putArguments(
                                mapOf(
                                    NavArgument.ActivityId to activityId.toString(),
                                    NavArgument.ReplyId to id.toStringOrZero(),
                                    NavArgument.Text to text
                                )
                            )
                        )
                    },
                    navigateToFullscreenImage = navigateToFullscreenImage,
                )
            }
        }

        composable(
            route = NavDestination.PublishActivity.route(),
            arguments = NavDestination.PublishActivity.namedNavArguments
        ) { navEntry ->
            if (isLoggedIn) {
                val id = navEntry.getIntArg(
                    NavDestination.PublishActivity.findDestArgument(NavArgument.ActivityId)
                )
                PublishActivityView(
                    id = if (id != 0) id else null,
                    text = navEntry.getStringArg(
                        NavDestination.PublishActivity.findDestArgument(NavArgument.Text)
                    ),
                    navigateBack = navigateBack
                )
            } else {
                LoginView()
            }
        }

        composable(
            route = NavDestination.PublishActivityReply.route(),
            arguments = NavDestination.PublishActivityReply.namedNavArguments
        ) { navEntry ->
            if (isLoggedIn) {
                val id = navEntry.getIntArg(
                    NavDestination.PublishActivityReply.findDestArgument(NavArgument.ReplyId)
                )
                PublishActivityView(
                    activityId = navEntry.getIntArg(
                        NavDestination.PublishActivityReply.findDestArgument(NavArgument.ActivityId)
                    ),
                    id = if (id != 0) id else null,
                    text = navEntry.getStringArg(
                        NavDestination.PublishActivityReply.findDestArgument(NavArgument.Text)
                    ),
                    navigateBack = navigateBack
                )
            } else {
                LoginView()
            }
        }

        composable(
            route = NavDestination.PublishThreadComment.route(),
            arguments = NavDestination.PublishThreadComment.namedNavArguments
        ) { navEntry ->
            if (isLoggedIn) {
                val commentId = navEntry.getIntArg(
                    NavDestination.PublishThreadComment.findDestArgument(NavArgument.CommentId)
                )
                PublishCommentView(
                    threadId = navEntry.getIntArg(
                        NavDestination.PublishThreadComment.findDestArgument(NavArgument.ThreadId)
                    ),
                    parentCommentId = null,
                    id = commentId,
                    text = navEntry.getStringArg(
                        NavDestination.PublishThreadComment.findDestArgument(NavArgument.Text)
                    ),
                    navigateBack = navigateBack,
                )
            } else {
                LoginView()
            }
        }

        composable(
            route = NavDestination.PublishCommentReply.route(),
            arguments = NavDestination.PublishCommentReply.namedNavArguments
        ) { navEntry ->
            if (isLoggedIn) {
                val commentId = navEntry.getIntArg(
                    NavDestination.PublishCommentReply.findDestArgument(NavArgument.CommentId)
                )
                PublishCommentView(
                    threadId = null,
                    parentCommentId = navEntry.getIntArg(
                        NavDestination.PublishCommentReply.findDestArgument(NavArgument.ParentCommentId)
                    ),
                    id = commentId,
                    text = navEntry.getStringArg(
                        NavDestination.PublishCommentReply.findDestArgument(NavArgument.Text)
                    ),
                    navigateBack = navigateBack,
                )
            } else {
                LoginView()
            }
        }
    }
}