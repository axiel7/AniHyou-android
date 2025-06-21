package com.axiel7.anihyou.feature.mediadetails.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.activity.text
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.activity.ActivityFeedItem
import com.axiel7.anihyou.core.ui.composables.activity.ActivityItemPlaceholder
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MediaActivityView(
    arguments: Routes.MediaActivity,
    navActionManager: NavActionManager
) {
    val viewModel: MediaActivityViewModel = koinViewModel(parameters = { parametersOf(arguments) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MediaActivityContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaActivityContent(
    uiState: MediaActivityUiState,
    event: MediaActivityEvent?,
    navActionManager: NavActionManager,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.activity),
        navigationIcon = {
            BackIconButton(
                onClick = navActionManager::goBack
            )
        },
        actions = {
            IconButton(onClick = { event?.setIsMine(!uiState.isMine) }) {
                Icon(
                    painter = painterResource(
                        id = if (uiState.isMine) R.drawable.person_filled_24
                        else R.drawable.person_24
                    ),
                    contentDescription = null
                )
            }
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            if (uiState.isLoading) {
                items(10) {
                    ActivityItemPlaceholder(
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            items(
                items = uiState.activities,
                contentType = { it }
            ) { item ->
                ActivityFeedItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    type = ActivityType.MEDIA_LIST,
                    username = item.user?.name,
                    avatarUrl = item.user?.avatar?.medium,
                    createdAt = item.createdAt,
                    text = item.text(),
                    replyCount = item.replyCount,
                    likeCount = item.likeCount,
                    isLiked = item.isLiked,
                    mediaCoverUrl = item.media?.coverImage?.medium,
                    showMenu = uiState.isMine,
                    onClick = {
                        navActionManager.toActivityDetails(item.id)
                    },
                    onClickUser = {
                        item.userId?.let(navActionManager::toUserDetails)
                    },
                    onClickLike = {
                        event?.toggleLikeActivity(item.id)
                    },
                    onClickDelete = {
                        event?.deleteActivity(item.id)
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
            item(contentType = { 0 }) {
                if (uiState.hasNextPage) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    LaunchedEffect(uiState.isLoading) {
                        if (!uiState.isLoading) event?.onLoadMore()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MediaActivityViewPreview() {
    AniHyouTheme {
        Surface {
            MediaActivityContent(
                uiState = MediaActivityUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}