package com.axiel7.anihyou.ui.screens.mediadetails.reviewthread

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.MediaReviewsQuery
import com.axiel7.anihyou.MediaThreadsQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.ui.composables.post.POST_ITEM_HEIGHT
import com.axiel7.anihyou.ui.composables.post.PostItem
import com.axiel7.anihyou.ui.composables.post.PostItemPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.format

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReviewThreadListView(
    mediaThreads: List<MediaThreadsQuery.Thread>,
    mediaReviews: List<MediaReviewsQuery.Node>,
    isLoadingThreads: Boolean,
    isLoadingReviews: Boolean,
    navigateToReviewDetails: (Int) -> Unit,
    navigateToThreadDetails: (Int) -> Unit,
) {
    val reviewsListState = rememberLazyGridState()
    val threadsListState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        InfoTitle(text = stringResource(R.string.threads))
        if (isLoadingThreads || mediaThreads.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp),
                state = threadsListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                flingBehavior = rememberSnapFlingBehavior(lazyListState = threadsListState),
            ) {
                items(
                    items = mediaThreads,
                    key = { it.basicThreadDetails.id },
                    contentType = { it }
                ) { item ->
                    PostItem(
                        title = item.basicThreadDetails.title ?: "",
                        author = item.basicThreadDetails.user?.name ?: "",
                        subtitle = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                TextIconHorizontal(
                                    text = item.basicThreadDetails.replyCount?.format() ?: "0",
                                    icon = R.drawable.chat_bubble_24,
                                    iconPadding = PaddingValues(start = 8.dp, end = 4.dp),
                                    fontSize = 15.sp
                                )
                                TextIconHorizontal(
                                    text = item.basicThreadDetails.viewCount?.format() ?: "0",
                                    icon = R.drawable.visibility_24,
                                    modifier = Modifier.padding(end = 8.dp),
                                    iconPadding = PaddingValues(start = 8.dp, end = 4.dp),
                                    fontSize = 15.sp
                                )
                            }
                        },
                        onClick = {
                            navigateToThreadDetails(item.basicThreadDetails.id)
                        }
                    )
                }
                if (isLoadingThreads) {
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

        InfoTitle(text = stringResource(R.string.reviews))
        if (isLoadingReviews || mediaReviews.isNotEmpty()) {
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
                    items = mediaReviews,
                    key = { it.id },
                    contentType = { it }
                ) { item ->
                    PostItem(
                        title = item.summary ?: "",
                        author = item.user?.name ?: "",
                        subtitle = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                TextIconHorizontal(
                                    text = item.score?.toString() ?: "0",
                                    icon = R.drawable.star_filled_20,
                                    iconPadding = PaddingValues(0.dp),
                                    fontSize = 15.sp
                                )
                                TextIconHorizontal(
                                    text = item.rating?.format() ?: "0",
                                    icon = R.drawable.thumb_up_filled_20,
                                    modifier = Modifier.padding(end = 8.dp),
                                    iconPadding = PaddingValues(start = 8.dp, end = 4.dp),
                                    fontSize = 15.sp
                                )
                            }
                        },
                        onClick = {
                            navigateToReviewDetails(item.id)
                        }
                    )
                }
                if (isLoadingReviews) {
                    items(4) {
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
                Text(text = stringResource(R.string.no_reviews))
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
                mediaReviews = emptyList(),
                mediaThreads = emptyList(),
                isLoadingThreads = true,
                isLoadingReviews = true,
                navigateToReviewDetails = {},
                navigateToThreadDetails = {}
            )
        }
    }
}