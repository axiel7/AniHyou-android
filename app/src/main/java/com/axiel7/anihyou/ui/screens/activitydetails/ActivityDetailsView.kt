package com.axiel7.anihyou.ui.screens.activitydetails

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.screens.activitydetails.composables.ActivityTextView
import com.axiel7.anihyou.ui.screens.activitydetails.composables.ActivityTextViewPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsView(
    navigateBack: () -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateToPublishActivityReply: (Int?, String?) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    val viewModel: ActivityDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val listState = rememberLazyListState()
    val expandedFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.activity),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(R.string.reply))
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.reply_24),
                        contentDescription = stringResource(R.string.reply)
                    )
                },
                onClick = { navigateToPublishActivityReply(null, null) },
                expanded = expandedFab
            )
        },
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .padding(padding),
            state = listState,
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            item {
                if (uiState.details != null) {
                    ActivityTextView(
                        text = uiState.details?.text ?: "",
                        username = uiState.details?.username,
                        avatarUrl = uiState.details?.avatarUrl,
                        createdAt = uiState.details?.createdAt ?: 0,
                        replyCount = uiState.details?.replyCount,
                        likeCount = uiState.details?.likeCount ?: 0,
                        isLiked = uiState.details?.isLiked,
                        onClickUser = {
                            uiState.details?.userId?.let(navigateToUserDetails)
                        },
                        onClickLike = {
                            viewModel.toggleLikeActivity()
                        },
                        navigateToFullscreenImage = navigateToFullscreenImage
                    )
                } else {
                    ActivityTextViewPlaceholder()
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
            items(
                items = viewModel.replies,
                key = { it.id },
                contentType = { it }
            ) { item ->
                ActivityTextView(
                    text = item.text ?: "",
                    username = item.user?.name,
                    avatarUrl = item.user?.avatar?.medium,
                    createdAt = item.createdAt,
                    replyCount = null,
                    likeCount = item.likeCount,
                    isLiked = item.isLiked,
                    onClickUser = {
                        item.userId?.let(navigateToUserDetails)
                    },
                    onClickLike = {
                        viewModel.toggleLikeReply(item.id)
                    },
                    navigateToFullscreenImage = navigateToFullscreenImage
                )
            }
        }
    }
}

@Preview
@Composable
fun ActivityDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            ActivityDetailsView(
                navigateBack = {},
                navigateToUserDetails = {},
                navigateToPublishActivityReply = { _, _ -> },
                navigateToFullscreenImage = {},
            )
        }
    }
}