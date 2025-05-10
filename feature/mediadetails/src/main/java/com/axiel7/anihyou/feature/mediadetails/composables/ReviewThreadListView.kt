package com.axiel7.anihyou.feature.mediadetails.composables

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampIntervalSinceNow
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.model.activity.text
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.InfoTitle
import com.axiel7.anihyou.core.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.core.ui.composables.list.HorizontalListHeader
import com.axiel7.anihyou.core.ui.composables.post.POST_ITEM_HEIGHT
import com.axiel7.anihyou.core.ui.composables.post.PostItem
import com.axiel7.anihyou.core.ui.composables.post.PostItemPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.secondsToLegibleText
import com.axiel7.anihyou.feature.mediadetails.MediaDetailsUiState
import java.time.temporal.ChronoUnit

@Composable
fun ReviewThreadListView(
    uiState: MediaDetailsUiState,
    navActionManager: NavActionManager,
) {
    val reviewsListState = rememberLazyGridState()
    val threadsListState = rememberLazyListState()
    val activityListState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        InfoTitle(text = stringResource(R.string.threads))
        if (uiState.isLoadingThreads || uiState.threads.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp),
                state = threadsListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = threadsListState),
            ) {
                items(
                    items = uiState.threads,
                    contentType = { it }
                ) { item ->
                    PostItem(
                        title = item.basicThreadDetails.title.orEmpty(),
                        author = item.basicThreadDetails.user?.name.orEmpty(),
                        avatarUrl = item.basicThreadDetails.user?.avatar?.medium.orEmpty(),
                        subtitle = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                TextIconHorizontal(
                                    text = item.basicThreadDetails.totalReplies?.format().orEmpty(),
                                    icon = R.drawable.chat_bubble_24,
                                    iconPadding = PaddingValues(start = 8.dp, end = 4.dp),
                                    fontSize = 15.sp
                                )
                                TextIconHorizontal(
                                    text = item.basicThreadDetails.viewCount?.format().orEmpty(),
                                    icon = R.drawable.visibility_24,
                                    modifier = Modifier.padding(end = 8.dp),
                                    iconPadding = PaddingValues(start = 8.dp, end = 4.dp),
                                    fontSize = 15.sp
                                )
                            }
                        },
                        onClick = {
                            navActionManager.toThreadDetails(item.basicThreadDetails.id)
                        }
                    )
                }
                if (uiState.isLoadingThreads) {
                    items(2) {
                        PostItemPlaceholder()
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .height(POST_ITEM_HEIGHT.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.no_threads))
            }
        }

        if (uiState.isLoadingReviews || uiState.reviews.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.reviews))
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
                    .height((POST_ITEM_HEIGHT * 2).dp),
                state = reviewsListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = uiState.reviews,
                    contentType = { it }
                ) { item ->
                    PostItem(
                        title = item.summary.orEmpty(),
                        author = item.user?.name.orEmpty(),
                        avatarUrl = item.user?.avatar?.medium.orEmpty(),
                        subtitle = {
                            TextIconHorizontal(
                                text = item.score?.format().orEmpty(),
                                icon = R.drawable.star_filled_20,
                                iconPadding = PaddingValues(0.dp),
                                fontSize = 15.sp
                            )
                        },
                        onClick = {
                            navActionManager.toReviewDetails(item.id)
                        }
                    )
                }
                if (uiState.isLoadingReviews) {
                    items(4) {
                        PostItemPlaceholder()
                    }
                }
            }
        }

        if (uiState.activity.isNotEmpty()) {
            HorizontalListHeader(
                text = stringResource(R.string.activity),
                onClick = {
                    uiState.details?.id?.let(navActionManager::toMediaActivity)
                }
            )
            LazyRow(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp),
                state = activityListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = activityListState),
            ) {
                items(
                    items = uiState.activity,
                    contentType = { it }
                ) { item ->
                    PostItem(
                        title = item.text(),
                        author = item.user?.name.orEmpty(),
                        avatarUrl = item.user?.avatar?.medium.orEmpty(),
                        subtitle = {
                            Text(
                                text = item.createdAt.toLong().timestampIntervalSinceNow()
                                    .secondsToLegibleText(
                                        maxUnit = ChronoUnit.WEEKS,
                                        isFutureDate = false
                                    ),
                                fontSize = 15.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        },
                        onClick = {
                            navActionManager.toActivityDetails(item.id)
                        }
                    )
                }
            }
        }
    }//: Column
}

@Preview
@Composable
fun ReviewThreadListViewPreview() {
    AniHyouTheme {
        Surface {
            ReviewThreadListView(
                uiState = MediaDetailsUiState(
                    isLoadingReviews = true,
                    isLoadingThreads = true
                ),
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}