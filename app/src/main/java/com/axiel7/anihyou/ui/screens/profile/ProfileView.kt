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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.hexColor
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.ShareIconButton
import com.axiel7.anihyou.ui.composables.TopBannerView
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

private enum class ProfileInfoType {
    ABOUT, ACTIVITY, STATS, FAVORITES, SOCIAL;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(ABOUT, icon = R.drawable.info_24),
            TabRowItem(ACTIVITY, icon = R.drawable.forum_24),
            TabRowItem(STATS, icon = R.drawable.bar_chart_24),
            TabRowItem(FAVORITES, icon = R.drawable.star_24),
            TabRowItem(SOCIAL, icon = R.drawable.group_24)
        )
    }
}

const val USER_DETAILS_DESTINATION = "user?id={id}?name={name}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(
    modifier: Modifier = Modifier,
    userId: Int? = null,
    username: String? = null,
    navigateToSettings: () -> Unit = {},
    navigateToFullscreenImage: (String) -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    navigateToStudioDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateToUserMediaList: ((MediaType, Int) -> Unit)?,
    navigateBack: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val viewModel: ProfileViewModel = viewModel()
    val isMyProfile by remember { derivedStateOf { userId == null && username == null } }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    LaunchedEffect(userId, username) {
        if (userId != null) viewModel.getUserInfo(userId)
        else if (username != null) viewModel.getUserInfo(username)
        else viewModel.getMyUserInfo()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    if (!isMyProfile) BackIconButton(onClick = navigateBack)
                },
                actions = {
                    ShareIconButton(url = viewModel.userInfo?.siteUrl ?: "")
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
                    imageUrl = viewModel.userInfo?.bannerImage,
                    modifier = Modifier.clickable {
                        viewModel.userInfo?.bannerImage?.let { navigateToFullscreenImage(it) }
                    },
                    fallbackColor = colorFromHex(viewModel.userInfo?.hexColor()),
                    height = padding.calculateTopPadding() + 100.dp
                )
                PersonImage(
                    url = viewModel.userInfo?.avatar?.large,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .size(PERSON_IMAGE_SIZE_SMALL.dp)
                        .clickable {
                            viewModel.userInfo?.avatar?.large?.let { navigateToFullscreenImage(it) }
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
                        text = viewModel.userInfo?.name ?: "Loading",
                        modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (viewModel.userInfo?.isFollower == true) {
                        Text(
                            text = stringResource(R.string.follows_you),
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 14.sp
                        )
                    }
                }

                if (isMyProfile) {
                    OutlinedIconButton(
                        onClick = navigateToSettings,
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
                    if (viewModel.userInfo?.isFollowing == true) {
                        OutlinedButton(
                            onClick = { scope.launch { viewModel.toggleFollow() } },
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            Text(text = stringResource(R.string.following))
                        }
                    } else if (viewModel.userInfo?.isFollowing == false) {
                        Button(
                            onClick = { scope.launch { viewModel.toggleFollow() } },
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            Text(text = stringResource(R.string.follow))
                        }
                    }
                }
            }//: Row

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedButtons(
                    items = ProfileInfoType.tabRows,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    defaultSelectedIndex = selectedTabIndex,
                    onItemSelection = {
                        selectedTabIndex = it
                    }
                )
                when (ProfileInfoType.tabRows[selectedTabIndex].value) {
                    ProfileInfoType.ABOUT ->
                        UserAboutView(
                            aboutHtml = viewModel.userInfo?.about,
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                            isLoading = viewModel.isLoading,
                            navigateToUserMediaList = if (navigateToUserMediaList != null) {
                                { mediaType -> navigateToUserMediaList(mediaType, viewModel.userId) }
                            } else null,
                        )
                    ProfileInfoType.ACTIVITY ->
                        UserActivityView(
                            viewModel = viewModel,
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                            navigateToMediaDetails = navigateToMediaDetails
                        )
                    ProfileInfoType.STATS ->
                        UserStatsView(
                            userId = viewModel.userId,
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                        )
                    ProfileInfoType.FAVORITES ->
                        UserFavoritesView(
                            userId = viewModel.userId,
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                            navigateToMediaDetails = navigateToMediaDetails,
                            navigateToCharacterDetails = navigateToCharacterDetails,
                            navigateToStaffDetails = navigateToStaffDetails,
                            navigateToStudioDetails = navigateToStudioDetails,
                        )
                    ProfileInfoType.SOCIAL ->
                        UserSocialView(
                            userId = viewModel.userId,
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                            navigateToUserDetails = navigateToUserDetails
                        )
                }
            }//: Column
        }//: Column
    }//: Scaffold
}

@Preview
@Composable
fun ProfileViewPreview() {
    AniHyouTheme {
        Surface {
            ProfileView(
                navigateToFullscreenImage = {},
                navigateToMediaDetails = {},
                navigateToCharacterDetails = {},
                navigateToStaffDetails = {},
                navigateToStudioDetails = {},
                navigateToUserDetails = {},
                navigateToUserMediaList = { _, _ -> }
            )
        }
    }
}