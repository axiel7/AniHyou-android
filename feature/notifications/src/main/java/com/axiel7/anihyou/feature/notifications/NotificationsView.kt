package com.axiel7.anihyou.feature.notifications

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.core.network.type.NotificationType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.core.ui.utils.DateUtils.timestampIntervalSinceNow
import com.axiel7.anihyou.feature.notifications.composables.NotificationItem
import com.axiel7.anihyou.feature.notifications.composables.NotificationItemPlaceholder
import org.koin.androidx.compose.koinViewModel
import java.time.temporal.ChronoUnit

@Composable
fun NotificationsView(
    navActionManager: NavActionManager,
) {
    val viewModel: NotificationsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NotificationsContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsContent(
    uiState: NotificationsUiState,
    event: NotificationsEvent?,
    navActionManager: NavActionManager,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val listState = rememberLazyListState()
    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.notifications),
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
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
                bottom = padding.calculateBottomPadding()
            )
        ) {
            item(
                contentType = uiState.type
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 8.dp)
                ) {
                    NotificationTypeGroup.entries.forEach {
                        FilterSelectionChip(
                            selected = uiState.type == it,
                            text = it.localized(),
                            onClick = { event?.setType(it) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
            if (uiState.isLoading) {
                items(10) {
                    NotificationItemPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }
            items(
                items = uiState.notifications,
                contentType = { it }
            ) { item ->
                NotificationItem(
                    title = item.text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    imageUrl = item.imageUrl,
                    subtitle = item.createdAt?.toLong()?.timestampIntervalSinceNow()
                        ?.secondsToLegibleText(
                            maxUnit = ChronoUnit.WEEKS,
                            isFutureDate = false
                        ),
                    isUnread = item.isUnread,
                    onClick = {
                        when (item.type) {
                            NotificationType.AIRING,
                            NotificationType.RELATED_MEDIA_ADDITION,
                            NotificationType.MEDIA_DATA_CHANGE,
                            NotificationType.MEDIA_MERGE,
                            NotificationType.MEDIA_DELETION ->
                                navActionManager.toMediaDetails(item.contentId)

                            NotificationType.THREAD_SUBSCRIBED,
                            NotificationType.THREAD_LIKE,
                            NotificationType.THREAD_COMMENT_MENTION,
                            NotificationType.THREAD_COMMENT_REPLY,
                            NotificationType.THREAD_COMMENT_LIKE ->
                                navActionManager.toThreadDetails(item.contentId)

                            NotificationType.ACTIVITY_MESSAGE,
                            NotificationType.ACTIVITY_REPLY,
                            NotificationType.ACTIVITY_MENTION,
                            NotificationType.ACTIVITY_LIKE,
                            NotificationType.ACTIVITY_REPLY_LIKE,
                            NotificationType.ACTIVITY_REPLY_SUBSCRIBED ->
                                navActionManager.toActivityDetails(item.contentId)

                            NotificationType.FOLLOWING ->
                                navActionManager.toUserDetails(item.contentId)

                            else -> {}
                        }
                    },
                    onClickImage = {
                        when (item.type) {
                            NotificationType.FOLLOWING ->
                                navActionManager.toUserDetails(item.contentId)

                            NotificationType.ACTIVITY_MESSAGE,
                            NotificationType.ACTIVITY_MENTION,
                            NotificationType.ACTIVITY_REPLY,
                            NotificationType.ACTIVITY_LIKE,
                            NotificationType.THREAD_COMMENT_MENTION,
                            NotificationType.THREAD_COMMENT_LIKE,
                            NotificationType.THREAD_LIKE ->
                                navActionManager.toUserDetails(
                                    item.secondaryContentId ?: item.contentId
                                )

                            else -> {}
                        }
                    }
                )
            }
        }//:LazyColumn
    }//:Scaffold
}

@Preview
@Composable
fun NotificationsViewPreview() {
    AniHyouTheme {
        Surface {
            NotificationsContent(
                uiState = NotificationsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}