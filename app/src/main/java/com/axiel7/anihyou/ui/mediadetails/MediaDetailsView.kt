package com.axiel7.anihyou.ui.mediadetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.axiel7.anihyou.data.model.isAnime
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_BIG_HEIGHT
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_BIG_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaPoster
import com.axiel7.anihyou.ui.composables.RoundedTabRowIndicator
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
import com.axiel7.anihyou.utils.DateUtils.formatted
import com.axiel7.anihyou.utils.DateUtils.minutesToLegibleText
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.NumberUtils
import com.axiel7.anihyou.utils.StringUtils.htmlStripped
import com.axiel7.anihyou.utils.UNKNOWN_CHAR
import kotlinx.coroutines.launch

const val MEDIA_DETAILS_DESTINATION = "details/{media_id}"

private enum class DetailsType {
    INFO, STAFF_CHARACTERS, RELATIONS, STATS, REVIEWS;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(INFO, icon = R.drawable.info_24),
            TabRowItem(STAFF_CHARACTERS, icon = R.drawable.group_24),
            TabRowItem(RELATIONS, icon = R.drawable.shuffle_24),
            TabRowItem(STATS, icon = R.drawable.bar_chart_24),
            TabRowItem(REVIEWS, icon = R.drawable.rate_review_24)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediaDetailsView(
    mediaId: Int,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: MediaDetailsViewModel = viewModel()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val pagerState = rememberPagerState()

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
    val isCurrentLanguageEn = remember { getCurrentLanguageTag()?.startsWith("en") }

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
             TopAppBar(
                 title = {},
                 navigationIcon = {
                     BackIconButton(onClick = navigateBack)
                 },
                 colors = TopAppBarDefaults.topAppBarColors(
                     containerColor = Color.Transparent,
                     scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                 ),
                 scrollBehavior = topAppBarScrollBehavior
             )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { scope.launch { sheetState.show() } }) {
                Icon(
                    painter = painterResource(if (isNewEntry) R.drawable.add_24
                    else R.drawable.edit_24),
                    contentDescription = "edit"
                )
                Text(
                    text = if (isNewEntry) stringResource(R.string.add)
                    else viewModel.mediaDetails?.mediaListEntry?.basicMediaListEntry?.status?.localized()
                        ?: stringResource(R.string.edit),
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
                            listOf(banner_shadow_color, MaterialTheme.colorScheme.surface)
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
                        icon = if (viewModel.mediaDetails?.basicMediaDetails?.isAnime() == true)
                            R.drawable.live_tv_24
                        else R.drawable.book_24,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading)
                    )
                    TextIconHorizontal(
                        text = viewModel.mediaDetails?.basicMediaDetails?.durationText() ?: UNKNOWN_CHAR,
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
                    .padding(horizontal = 16.dp)
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
                    VerticalDivider(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .height(dividerHeight.dp)
                    )
                }
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
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                    .defaultPlaceholder(visible = viewModel.isLoading),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLinesSynopsis
            )
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
            }//: Row

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp)
            ) {
                viewModel.mediaDetails?.genres?.forEach {
                    AssistChip(
                        onClick = { },
                        label = { Text(text = it ?: "") },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Other info
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    RoundedTabRowIndicator(tabPositions[pagerState.currentPage])
                }
            ) {
                DetailsType.tabRows.forEachIndexed { index, item ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        icon = { Icon(painter = painterResource(item.icon!!), contentDescription = item.value.name) },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }//: TabRow

            HorizontalPager(
                pageCount = DetailsType.tabRows.size,
                state = pagerState,
                key = { DetailsType.tabRows[it].value }
            ) {
                when (DetailsType.tabRows[it].value) {
                    DetailsType.INFO ->
                        MediaInformationView(
                            viewModel = viewModel
                        )
                    DetailsType.STAFF_CHARACTERS ->
                        CharacterStaffView(
                            mediaId = mediaId,
                            viewModel = viewModel
                        )
                    DetailsType.RELATIONS -> MediaRelationsView(viewModel = viewModel)
                    DetailsType.STATS -> MediaStatsView(viewModel = viewModel)
                    DetailsType.REVIEWS -> ReviewThreadView(viewModel = viewModel)
                }
            }//: Pager
        }//: Column
    }//: Scaffold

    if (sheetState.isVisible && viewModel.mediaDetails != null) {
        EditMediaSheet(
            sheetState = sheetState,
            mediaDetails = viewModel.mediaDetails!!.basicMediaDetails,
            listEntry = viewModel.mediaDetails?.mediaListEntry?.basicMediaListEntry,
            onDismiss = { scope.launch { sheetState.hide() } }
        )
    }

    LaunchedEffect(mediaId) {
        viewModel.getDetails(mediaId)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaInformationView(
    viewModel: MediaDetailsViewModel
) {
    var showSpoiler by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        InfoTitle(text = stringResource(R.string.information))

        MediaInfoItemView(
            title = stringResource(R.string.duration),
            info = viewModel.mediaDetails?.duration?.toLong()?.minutesToLegibleText()
        )
        MediaInfoItemView(
            title = stringResource(R.string.start_date),
            info = viewModel.mediaDetails?.startDate?.fuzzyDate?.formatted()
        )
        MediaInfoItemView(
            title = stringResource(R.string.end_date),
            info = viewModel.mediaDetails?.endDate?.fuzzyDate?.formatted()
        )
        if (viewModel.mediaDetails?.basicMediaDetails?.isAnime() == true) {
            MediaInfoItemView(
                title = stringResource(R.string.season),
                info = "${viewModel.mediaDetails?.season?.localized()} ${viewModel.mediaDetails?.seasonYear}"
            )
            MediaInfoItemView(
                title = stringResource(R.string.studios),
                info = viewModel.getStudios()?.joinToString { it.name }
            )
            MediaInfoItemView(
                title = stringResource(R.string.producers),
                info = viewModel.getProducers()?.joinToString { it.name }
            )
        }
        MediaInfoItemView(
            title = stringResource(R.string.source),
            info = viewModel.mediaDetails?.source?.localized()
        )
        MediaInfoItemView(
            title = stringResource(R.string.romaji),
            info = viewModel.mediaDetails?.title?.romaji
        )
        MediaInfoItemView(
            title = stringResource(R.string.english),
            info = viewModel.mediaDetails?.title?.english
        )
        MediaInfoItemView(
            title = stringResource(R.string.native_title),
            info = viewModel.mediaDetails?.title?.native
        )

        // Tags
        InfoTitle(
            text = stringResource(R.string.tags),
            trailingIcon = {
                TextButton(onClick = { showSpoiler = !showSpoiler }) {
                    Text(text = stringResource(
                        if (showSpoiler) R.string.hide_spoiler else R.string.show_spoiler)
                    )
                }
            }
        )
        FlowRow(
            Modifier.padding(horizontal = 8.dp)
        ) {
            viewModel.mediaDetails?.tags?.forEach { tag ->
                if (tag != null && (showSpoiler || tag.isMediaSpoiler == false)) {
                    if (tag.isMediaSpoiler == false) {
                        ElevatedAssistChip(
                            onClick = { },
                            label = { Text(text = tag.name) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            leadingIcon = { Text(text = "${tag.rank}%") }
                        )
                    }
                    else {
                        AssistChip(
                            onClick = { },
                            label = { Text(text = tag.name) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            leadingIcon = { Text(text = "${tag.rank}%") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaInfoItemView(
    title: String,
    info: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(modifier)
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = info ?: stringResource(R.string.unknown),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun InfoTitle(
    text: String,
    trailingIcon: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        trailingIcon()
    }
}

@Preview
@Composable
fun MediaDetailsViewPreview() {
    AniHyouTheme {
        MediaDetailsView(
            mediaId = 1,
            navigateBack = {}
        )
    }
}