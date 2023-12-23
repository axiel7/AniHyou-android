package com.axiel7.anihyou.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserActivityQuery
import com.axiel7.anihyou.data.model.user.hexColor
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.TopBannerView
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.composables.common.ShareIconButton
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_SMALL
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.screens.profile.about.UserAboutView
import com.axiel7.anihyou.ui.screens.profile.activity.UserActivityView
import com.axiel7.anihyou.ui.screens.profile.favorites.UserFavoritesView
import com.axiel7.anihyou.ui.screens.profile.social.UserSocialView
import com.axiel7.anihyou.ui.screens.profile.stats.UserStatsView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ColorUtils.colorFromHex
import kotlinx.coroutines.launch

@Composable
fun ProfileView(
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        userActivities = viewModel.userActivities,
        uiState = uiState,
        event = viewModel,
        modifier = modifier,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    userActivities: List<UserActivityQuery.Activity>,
    uiState: ProfileUiState,
    event: ProfileEvent?,
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val scope = rememberCoroutineScope()

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    if (uiState.isMyProfile == false) {
                        BackIconButton(onClick = navActionManager::goBack)
                    }
                },
                actions = {
                    ShareIconButton(url = uiState.userInfo?.siteUrl.orEmpty())
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                scrollBehavior = topAppBarScrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.BottomStart
            ) {
                TopBannerView(
                    imageUrl = uiState.userInfo?.bannerImage,
                    modifier = Modifier.clickable {
                        uiState.userInfo?.bannerImage?.let(navActionManager::toFullscreenImage)
                    },
                    fallbackColor = colorFromHex(uiState.userInfo?.hexColor()),
                    height = padding.calculateTopPadding() + 100.dp
                )
                PersonImage(
                    url = uiState.userInfo?.avatar?.large,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .size(PERSON_IMAGE_SIZE_SMALL.dp)
                        .clickable {
                            uiState.userInfo?.avatar?.large?.let(navActionManager::toFullscreenImage)
                        },
                    showShadow = true
                )
            }//: Box

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
                        .padding(horizontal = 16.dp)
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

                if (uiState.isMyProfile == true) {
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
                } else if (uiState.isMyProfile == false) {
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

            if (uiState.userInfo != null) {
                SegmentedButtons(
                    items = ProfileInfoType.tabRows,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                            navigateToUserMediaList = if (uiState.isMyProfile == false) {
                                { mediaType ->
                                    navActionManager.toUserMediaList(
                                        mediaType,
                                        uiState.userInfo.id,
                                        uiState.userInfo.mediaListOptions!!.scoreFormat!!
                                    )
                                }
                            } else null,
                        )

                    ProfileInfoType.ACTIVITY -> {
                        LaunchedEffect(uiState.page) {
                            if (uiState.page == 0) event?.onLoadMore()
                        }
                        UserActivityView(
                            activities = userActivities,
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

@Preview
@Composable
fun ProfileViewPreview() {
    AniHyouTheme {
        Surface {
            ProfileContent(
                userActivities = emptyList(),
                uiState = ProfileUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}