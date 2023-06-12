package com.axiel7.anihyou.ui.notifications

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.NotificationTypeGroup
import com.axiel7.anihyou.type.NotificationType
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.activity.ActivityItem
import com.axiel7.anihyou.ui.composables.activity.ActivityItemPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.showToast
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.DateUtils.timestampIntervalSinceNow

const val NOTIFICATIONS_DESTINATION = "notifications"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsView(
    navigateToMediaDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: NotificationsViewModel = viewModel()
    val listState = rememberLazyListState()

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getNotifications()
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.notifications),
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        scrollBehavior = topAppBarScrollBehavior,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 8.dp,
                bottom = padding.calculateBottomPadding()
            )
        ) {
            item(
                contentType = viewModel.type
            ) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    NotificationTypeGroup.values().forEach {
                        FilterSelectionChip(
                            selected = viewModel.type == it,
                            text = it.localized(),
                            onClick = {
                                viewModel.type = it
                                viewModel.resetPage()
                            }
                        )
                    }
                }
            }
            items(
                items = viewModel.notifications,
                contentType = { it }
            ) { item ->
                ActivityItem(
                    title = item.text,
                    imageUrl = item.imageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    subtitle = item.createdAt?.toLong()?.timestampIntervalSinceNow()?.secondsToLegibleText(),
                    onClick = {
                        when (item.type) {
                            NotificationType.AIRING,
                            NotificationType.RELATED_MEDIA_ADDITION,
                            NotificationType.MEDIA_DATA_CHANGE,
                            NotificationType.MEDIA_MERGE,
                            NotificationType.MEDIA_DELETION -> navigateToMediaDetails(item.contentId)
                            else -> {
                                context.showToast("Coming Soon")
                            }
                        }
                    },
                    onClickImage = {
                        when (item.type) {
                            NotificationType.FOLLOWING -> navigateToUserDetails(item.contentId)
                            NotificationType.ACTIVITY_MESSAGE,
                            NotificationType.ACTIVITY_MENTION,
                            NotificationType.ACTIVITY_REPLY,
                            NotificationType.ACTIVITY_LIKE,
                            NotificationType.THREAD_COMMENT_MENTION,
                            NotificationType.THREAD_COMMENT_LIKE,
                            NotificationType.THREAD_LIKE -> navigateToUserDetails(item.secondaryContentId!!)
                            else -> {}
                        }
                    }
                )
            }
            if (viewModel.isLoading) {
                items(10) {
                    ActivityItemPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    )
                }
            }
        }//:LazyColumn
    }//:Scaffold
}

@Preview
@Composable
fun NotificationsViewPreview() {
    AniHyouTheme {
        Surface {
            NotificationsView(
                navigateToMediaDetails = {},
                navigateToUserDetails = {},
                navigateBack = {}
            )
        }
    }
}