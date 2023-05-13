package com.axiel7.anihyou.ui.mediadetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.axiel7.anihyou.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.ui.composables.VerticalDivider
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.ui.theme.banner_shadow_color
import com.axiel7.anihyou.utils.ColorUtils.colorFromHex
import com.axiel7.anihyou.utils.ContextUtils.copyToClipBoard
import com.axiel7.anihyou.utils.ContextUtils.getCurrentLanguageTag
import com.axiel7.anihyou.utils.ContextUtils.openInGoogleTranslate
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.NumberUtils
import com.axiel7.anihyou.utils.StringUtils.htmlStripped
import com.axiel7.anihyou.utils.UNKNOWN_CHAR

const val MEDIA_DETAILS_DESTINATION = "details/{media_id}"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediaDetailsView(
    mediaId: Int
) {
    val context = LocalContext.current
    val viewModel: MediaDetailsViewModel = viewModel()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
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
        topBar = {
             TopAppBar(
                 title = { /*TODO*/ },
                 navigationIcon = {
                     IconButton(
                         onClick = { /*TODO*/ },
                     ) {
                         Icon(
                             painter = painterResource(R.drawable.arrow_back_24),
                             contentDescription = "back",
                             tint = Color.White
                         )
                     }
                 },
                 colors = TopAppBarDefaults.topAppBarColors(
                     containerColor = Color.Transparent,
                     scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                 ),
                 scrollBehavior = topAppBarScrollBehavior
             )
        },
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
                .verticalScroll(rememberScrollState())
                .padding(bottom = padding.calculateBottomPadding())
                .padding(bottom = 88.dp)
        ) {
            // Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(padding.calculateTopPadding() + 80.dp)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (viewModel.mediaDetails?.bannerImage != null) {
                    AsyncImage(
                        model = viewModel.mediaDetails?.bannerImage,
                        contentDescription = "banner",
                        placeholder = ColorPainter(MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (viewModel.mediaDetails?.coverImage?.color != null)
                                    colorFromHex(viewModel.mediaDetails?.coverImage?.color!!)
                                else MaterialTheme.colorScheme.outline
                            )
                            .fillMaxSize()
                    )
                }
                //top shadow
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(banner_shadow_color, Color.Transparent)
                        )
                    )
                )
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
                        text = viewModel.mediaDetails?.status.localized(),
                        icon = R.drawable.rss_feed_24,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading)
                    )
                }
            }//:Row

            // General info
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp)
            ) {
                val dividerHeight = 36
                viewModel.mediaDetails?.nextAiringEpisode?.let { nextAiringEpisode ->
                    TextSubtitleVertical(
                        text = stringResource(R.string.episode_in_time,
                            nextAiringEpisode.episode,
                            nextAiringEpisode.timeUntilAiring.toLong().secondsToLegibleText()
                        ),
                        subtitle = stringResource(R.string.airing),
                        isLoading = viewModel.isLoading
                    )
                }
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(dividerHeight.dp)
                )
                TextSubtitleVertical(
                    text = "${viewModel.mediaDetails?.averageScore ?: 0}%",
                    subtitle = stringResource(R.string.average_score),
                    isLoading = viewModel.isLoading
                )
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(dividerHeight.dp)
                )
                TextSubtitleVertical(
                    text = "${viewModel.mediaDetails?.meanScore ?: 0}%",
                    subtitle = stringResource(R.string.mean_score),
                    isLoading = viewModel.isLoading
                )
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(dividerHeight.dp)
                )
                TextSubtitleVertical(
                    text = NumberUtils.defaultNumberFormat.format(viewModel.mediaDetails?.popularity ?: 0),
                    subtitle = stringResource(R.string.popularity),
                    isLoading = viewModel.isLoading
                )
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(dividerHeight.dp)
                )
                TextSubtitleVertical(
                    text = NumberUtils.defaultNumberFormat.format(viewModel.mediaDetails?.favourites ?: 0),
                    subtitle = stringResource(R.string.favorites),
                    isLoading = viewModel.isLoading
                )
            }//: Row

            // Synopsis
            Text(
                text = viewModel.mediaDetails?.description?.htmlStripped() ?: stringResource(R.string.lorem_ipsun),
                modifier = Modifier
                    .padding(16.dp)
                    .defaultPlaceholder(visible = viewModel.isLoading),
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLinesSynopsis
            )
            val isCurrentLanguageEn = remember { getCurrentLanguageTag()?.startsWith("en") }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCurrentLanguageEn == false) {
                    IconButton(onClick = {
                        viewModel.mediaDetails?.description?.let {
                            context.openInGoogleTranslate(it.htmlStripped())
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.translate_24),
                            contentDescription = "translate"
                        )
                    }
                }
                else Spacer(modifier = Modifier.size(48.dp))

                IconButton(
                    onClick = {
                        maxLinesSynopsis = if (maxLinesSynopsis == 5) Int.MAX_VALUE else 5
                    }
                ) {
                    Icon(painter = painterResource(iconExpand), contentDescription = "expand")
                }

                IconButton(
                    onClick = {
                        viewModel.mediaDetails?.description?.let {
                            context.copyToClipBoard(it.htmlStripped())
                        }
                    }
                ) {
                    Icon(painter = painterResource(R.drawable.content_copy_24), contentDescription = "copy")
                }
            }
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