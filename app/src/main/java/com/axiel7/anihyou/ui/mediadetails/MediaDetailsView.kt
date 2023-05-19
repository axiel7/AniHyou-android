package com.axiel7.anihyou.ui.mediadetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.durationText
import com.axiel7.anihyou.data.model.isAnime
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.InfoItemView
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.ui.composables.TopBannerView
import com.axiel7.anihyou.ui.composables.VerticalDivider
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_BIG_HEIGHT
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_BIG_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaPoster
import com.axiel7.anihyou.ui.theme.AniHyouTheme
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

const val MEDIA_DETAILS_DESTINATION = "media_details/{media_id}"

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
    navigateToMediaDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String?) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
) {
    val context = LocalContext.current
    val viewModel: MediaDetailsViewModel = viewModel()

    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var selectedTabItem by remember { mutableStateOf(DetailsType.tabRows[0]) }

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

    LaunchedEffect(mediaId) {
        viewModel.getDetails(mediaId)
    }

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
            TopBannerView(
                imageUrl = viewModel.mediaDetails?.bannerImage,
                modifier = Modifier.clickable {
                    navigateToFullscreenImage(viewModel.mediaDetails?.bannerImage)
                },
                fallbackColor = colorFromHex(viewModel.mediaDetails?.coverImage?.color),
                height = padding.calculateTopPadding() + 80.dp
            )

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
                        .clickable {
                            navigateToFullscreenImage(viewModel.mediaDetails?.coverImage?.extraLarge)
                        }
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedButtons(
                    items = DetailsType.tabRows,
                    onItemSelection = {
                        selectedTabItem = it
                    }
                )
                when (selectedTabItem.value) {
                    DetailsType.INFO ->
                        MediaInformationView(
                            viewModel = viewModel
                        )

                    DetailsType.STAFF_CHARACTERS ->
                        CharacterStaffView(
                            mediaId = mediaId,
                            viewModel = viewModel,
                            navigateToCharacterDetails = navigateToCharacterDetails
                        )

                    DetailsType.RELATIONS ->
                        MediaRelationsView(
                            mediaId = mediaId,
                            viewModel = viewModel,
                            navigateToDetails = navigateToMediaDetails
                        )

                    DetailsType.STATS -> MediaStatsView(viewModel = viewModel)
                    DetailsType.REVIEWS -> ReviewThreadView(viewModel = viewModel)
                }
            }//: Column
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

        InfoItemView(
            title = stringResource(R.string.duration),
            info = viewModel.mediaDetails?.duration?.toLong()?.minutesToLegibleText(),
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.start_date),
            info = viewModel.mediaDetails?.startDate?.fuzzyDate?.formatted(),
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.end_date),
            info = viewModel.mediaDetails?.endDate?.fuzzyDate?.formatted(),
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        if (viewModel.mediaDetails?.basicMediaDetails?.isAnime() == true) {
            InfoItemView(
                title = stringResource(R.string.season),
                info = "${viewModel.mediaDetails?.season?.localized()} ${viewModel.mediaDetails?.seasonYear}"
            )
            InfoItemView(
                title = stringResource(R.string.studios),
                info = viewModel.getStudios()?.joinToString { it.name }
            )
            InfoItemView(
                title = stringResource(R.string.producers),
                info = viewModel.getProducers()?.joinToString { it.name }
            )
        }
        InfoItemView(
            title = stringResource(R.string.source),
            info = viewModel.mediaDetails?.source?.localized(),
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.romaji),
            info = viewModel.mediaDetails?.title?.romaji,
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.english),
            info = viewModel.mediaDetails?.title?.english,
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.native_title),
            info = viewModel.mediaDetails?.title?.native,
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
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
            Modifier
                .padding(horizontal = 8.dp)
                .animateContentSize()
        ) {
            viewModel.mediaDetails?.tags?.forEach { tag ->
                if (tag != null) {
                    if (tag.isMediaSpoiler == false) {
                        ElevatedAssistChip(
                            onClick = { },
                            label = { Text(text = tag.name) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            leadingIcon = { Text(text = "${tag.rank}%") }
                        )
                    }
                    else {
                        AnimatedVisibility(visible = showSpoiler) {
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
    }//: Column
}

@Preview
@Composable
fun MediaDetailsViewPreview() {
    AniHyouTheme {
        MediaDetailsView(
            mediaId = 1,
            navigateBack = {},
            navigateToMediaDetails = {},
            navigateToFullscreenImage = {},
            navigateToCharacterDetails = {}
        )
    }
}