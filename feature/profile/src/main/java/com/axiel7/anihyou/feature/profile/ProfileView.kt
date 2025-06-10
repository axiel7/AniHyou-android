package com.axiel7.anihyou.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.common.utils.ContextUtils.showToast
import com.axiel7.anihyou.core.model.user.hexColor
import com.axiel7.anihyou.core.resources.ColorUtils.colorFromHex
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.SegmentedButtons
import com.axiel7.anihyou.core.ui.composables.TopBannerView
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ShareIconButton
import com.axiel7.anihyou.core.ui.composables.common.singleClick
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.person.PERSON_IMAGE_SIZE_SMALL
import com.axiel7.anihyou.core.ui.composables.person.PersonImage
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.profile.about.UserAboutView
import com.axiel7.anihyou.feature.profile.activity.UserActivityView
import com.axiel7.anihyou.feature.profile.favorites.UserFavoritesView
import com.axiel7.anihyou.feature.profile.social.UserSocialView
import com.axiel7.anihyou.feature.profile.stats.UserStatsView
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileView(
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        uiState = uiState,
        event = viewModel,
        modifier = modifier,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    event: ProfileEvent?,
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val context = LocalContext.current
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val collapsedFraction by remember {
        derivedStateOf { topAppBarScrollBehavior.state.collapsedFraction }
    }
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues()

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            event?.onErrorDisplayed()
            context.showToast(uiState.error)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBannerView(
                imageUrl = uiState.userInfo?.bannerImage,
                modifier = Modifier.clickable {
                    uiState.userInfo?.bannerImage?.let(navActionManager::toFullscreenImage)
                },
                fallbackColor = colorFromHex(uiState.userInfo?.hexColor()),
                height = statusBarPadding.calculateTopPadding() + 100.dp
            )
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (!uiState.isMyProfile) {
                        BackIconButton(onClick = navActionManager::goBack)
                    }
                },
                actions = {
                    ShareIconButton(url = uiState.userInfo?.siteUrl.orEmpty())
                },
                windowInsets = WindowInsets.statusBars.only(WindowInsetsSides.Top),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
            )
            TopAppBar(
                title = {
                    MainProfileInfo(
                        uiState = uiState,
                        event = event,
                        navActionManager = navActionManager,
                        modifier = Modifier.offset {
                            val offset = collapsedFraction * 200
                            IntOffset(
                                x = 0,
                                y = -offset.toInt()
                            )
                        }
                    )
                },
                modifier = Modifier.padding(top = statusBarPadding.calculateTopPadding() + 24.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
        ) {
            if (uiState.userInfo != null) {
                SegmentedButtons(
                    items = ProfileInfoType.tabRows,
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    ),
                    selectedIndex = selectedTabIndex,
                    onItemSelection = {
                        selectedTabIndex = it
                    }
                )
                when (ProfileInfoType.tabRows[selectedTabIndex].value) {
                    ProfileInfoType.ABOUT ->
                        UserAboutView(
                            aboutHtml = uiState.userInfo.about,
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                            isLoading = uiState.isLoading,
                            onRefresh = { event?.onRefresh() },
                            navigateToUserMediaList = if (!uiState.isMyProfile) {
                                { mediaType ->
                                    navActionManager.toUserMediaList(
                                        mediaType,
                                        uiState.userInfo.id,
                                        uiState.userInfo.mediaListOptions!!.commonMediaListOptions.scoreFormat!!
                                    )
                                }
                            } else null,
                        )

                    ProfileInfoType.ACTIVITY -> {
                        LaunchedEffect(uiState.page) {
                            if (uiState.page == 0) event?.onLoadMore()
                        }
                        UserActivityView(
                            activities = uiState.activities,
                            uiState = uiState,
                            event = event,
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                            navActionManager = navActionManager,
                        )
                    }

                    ProfileInfoType.STATS ->
                        UserStatsView(
                            userId = uiState.userInfo.id,
                            nestedScrollConnection = topAppBarScrollBehavior.nestedScrollConnection,
                            navActionManager = navActionManager,
                        )

                    ProfileInfoType.FAVORITES ->
                        UserFavoritesView(
                            userId = uiState.userInfo.id,
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                            navActionManager = navActionManager,
                        )

                    ProfileInfoType.SOCIAL ->
                        UserSocialView(
                            userId = uiState.userInfo.id,
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                            navActionManager = navActionManager,
                        )
                }
            }
        }//: Column
    }//: Scaffold
}

@Composable
private fun MainProfileInfo(
    uiState: ProfileUiState,
    event: ProfileEvent?,
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    Column(modifier = modifier) {
        PersonImage(
            url = uiState.userInfo?.avatar?.large,
            modifier = Modifier
                .padding(start = 8.dp, top = 16.dp, end = 16.dp)
                .size(PERSON_IMAGE_SIZE_SMALL.dp)
                .clickable(onClick = singleClick {
                    uiState.userInfo?.avatar?.large?.let(navActionManager::toFullscreenImage)
                }),
            showShadow = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = uiState.userInfo?.name ?: "Loading",
                    modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                uiState.userInfo?.donatorTier?.let { donatorTier ->
                    if (donatorTier > 1) {
                        Text(
                            text = uiState.userInfo.donatorBadge
                                ?: stringResource(R.string.donator),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }
                if (uiState.userInfo?.isFollower == true) {
                    Text(
                        text = stringResource(R.string.follows_you),
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 14.sp
                    )
                }
            }//:Column

            if (uiState.isMyProfile) {
                OutlinedIconButton(
                    onClick = navActionManager::toSettings,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.settings_24),
                        contentDescription = stringResource(R.string.settings),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                if (uiState.userInfo?.isFollowing == true) {
                    OutlinedButton(
                        onClick = { scope.launch { event?.toggleFollow() } },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Text(text = stringResource(R.string.following))
                    }
                } else if (uiState.userInfo?.isFollowing == false) {
                    Button(
                        onClick = { scope.launch { event?.toggleFollow() } },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    ) {
                        Text(text = stringResource(R.string.follow))
                    }
                }
            }
        }//: Row
    }
}

@Preview
@Composable
fun ProfileViewPreview() {
    AniHyouTheme {
        Surface {
            ProfileContent(
                uiState = ProfileUiState(isMyProfile = false),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}