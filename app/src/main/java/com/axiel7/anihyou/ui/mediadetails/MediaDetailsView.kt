package com.axiel7.anihyou.ui.mediadetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.durationText
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.MEDIA_POSTER_BIG_HEIGHT
import com.axiel7.anihyou.ui.composables.MEDIA_POSTER_BIG_WIDTH
import com.axiel7.anihyou.ui.composables.MediaPoster
import com.axiel7.anihyou.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.ui.theme.banner_shadow_color
import com.axiel7.anihyou.utils.ContextUtils.copyToClipBoard
import com.axiel7.anihyou.utils.UNKNOWN_CHAR

const val MEDIA_DETAILS_DESTINATION = "details/{media_id}"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediaDetailsView(
    mediaId: Int
) {
    val context = LocalContext.current
    val viewModel: MediaDetailsViewModel = viewModel()
    val scrollState = rememberScrollState()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var maxLinesSynopsis by remember { mutableStateOf(5) }
    val iconExpand by remember {
        derivedStateOf {
            if (maxLinesSynopsis == 5) R.drawable.expand_more_24
            else R.drawable.expand_less_24
        }
    }
    val isNewEntry by remember {
        derivedStateOf { viewModel.mediaDetails?.mediaListEntry == null }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(if (isNewEntry) R.drawable.add_24
                    else R.drawable.edit_24),
                    contentDescription = "edit"
                )
                Text(
                    text = stringResource(if (isNewEntry) R.string.add else R.string.edit),
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(bottom = padding.calculateBottomPadding())
                .padding(bottom = 88.dp)
        ) {
            // Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(padding.calculateTopPadding() + 148.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                AsyncImage(
                    model = viewModel.mediaDetails?.bannerImage,
                    contentDescription = "banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                //top shadow
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(listOf(
                            banner_shadow_color, Color.Transparent
                        ))
                    )
                )
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.padding(top = padding.calculateTopPadding())
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back_24),
                        contentDescription = "back",
                        modifier = Modifier.shadow(4.dp),
                        tint = Color.White
                    )
                }
            }

            // Poster and basic info
            Row {
                MediaPoster(
                    url = viewModel.mediaDetails?.coverImage?.large,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .size(
                            width = MEDIA_POSTER_BIG_WIDTH.dp,
                            height = MEDIA_POSTER_BIG_HEIGHT.dp
                        )
                        .defaultPlaceholder(visible = viewModel.isLoading)
                        .clickable { /*TODO*/ }
                )
                Column {
                    Text(
                        text = viewModel.mediaDetails?.title?.userPreferred ?: "Loading",
                        modifier = Modifier
                            .padding(bottom = 8.dp, end = 8.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading)
                            .combinedClickable(
                                onLongClick = {
                                    viewModel.mediaDetails?.title?.userPreferred
                                        ?.let { context.copyToClipBoard(it) }
                                },
                                onClick = { }
                            ),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextIconHorizontal(
                        text = viewModel.mediaDetails?.format?.localized() ?: "Loading",
                        icon = if (viewModel.mediaDetails?.type == MediaType.ANIME) R.drawable.live_tv_24
                        else R.drawable.book_24,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading)
                    )
                    TextIconHorizontal(
                        text = viewModel.mediaDetails?.durationText() ?: UNKNOWN_CHAR,
                        icon = R.drawable.timer_24,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading)
                    )
                    TextIconHorizontal(
                        text = "${viewModel.mediaDetails?.meanScore ?: 0}%",
                        icon = R.drawable.star_24,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading)
                    )
                }
            }//:Row
        }
    }//: Scaffold

    LaunchedEffect(mediaId) {
        viewModel.getDetails(mediaId)
    }
}

@Preview
@Composable
fun MediaDetailsViewPreview() {
    AniHyouTheme {
        MediaDetailsView(
            mediaId = 1
        )
    }
}