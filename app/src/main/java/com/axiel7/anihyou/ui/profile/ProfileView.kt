package com.axiel7.anihyou.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.hexColor
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.composables.DefaultTabRowWithPager
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.TopBannerView
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_SMALL
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ColorUtils.colorFromHex

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

const val USER_DETAILS_DESTINATION = "profile/{id}"

@Composable
fun ProfileView(
    userId: Int? = null,
    navigateToSettings: () -> Unit = {},
) {
    val viewModel: ProfileViewModel = viewModel()
    val isMyProfile by remember { derivedStateOf { userId == null } }
    var selectedTabItem by remember { mutableStateOf(ProfileInfoType.tabRows[0]) }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.BottomStart
            ) {
                TopBannerView(
                    imageUrl = viewModel.userInfo?.bannerImage,
                    fallbackColor = colorFromHex(viewModel.userInfo?.hexColor()),
                    height = padding.calculateTopPadding() + 150.dp
                )
                PersonImage(
                    url = viewModel.userInfo?.avatar?.large,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .size(PERSON_IMAGE_SIZE_SMALL.dp),
                    showShadow = true
                )
            }//: Box

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = viewModel.userInfo?.name ?: "axiel7",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                )

                if (isMyProfile) {
                    OutlinedButton(
                        onClick = navigateToSettings,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(text = stringResource(R.string.settings))
                    }
                }
            }//: Row

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedButtons(
                    items = ProfileInfoType.tabRows,
                    onItemSelection = {
                        selectedTabItem = it
                    }
                )
                when (selectedTabItem.value) {
                    ProfileInfoType.ABOUT -> ProfileAboutView(aboutHtml = viewModel.userInfo?.about)
                    ProfileInfoType.ACTIVITY -> UserActivityView(viewModel = viewModel)
                    ProfileInfoType.STATS -> UserStatsView(viewModel = viewModel)
                    ProfileInfoType.FAVORITES -> UserFavoritesView(viewModel = viewModel)
                    ProfileInfoType.SOCIAL -> UserSocialView(viewModel = viewModel)
                }
            }//: Column
        }//: Column
    }//: Scaffold

    LaunchedEffect(userId) {
        if (userId == null) viewModel.getMyUserInfo()
        else viewModel.getUserInfo(userId)
    }
}

@Preview
@Composable
fun ProfileViewPreview() {
    AniHyouTheme {
        Surface {
            ProfileView()
        }
    }
}