package com.axiel7.anihyou.ui.screens.mediadetails

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.media.durationText
import com.axiel7.anihyou.data.model.media.isAnime
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.TabRowItem
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.ShareIconButton
import com.axiel7.anihyou.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.ui.composables.TopBannerView
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_BIG_HEIGHT
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_BIG_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaPoster
import com.axiel7.anihyou.ui.screens.mediadetails.characterstaff.MediaCharacterStaffView
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.screens.mediadetails.info.MediaInformationView
import com.axiel7.anihyou.ui.screens.mediadetails.related.MediaRelationsView
import com.axiel7.anihyou.ui.screens.mediadetails.reviewthread.ReviewThreadListView
import com.axiel7.anihyou.ui.screens.mediadetails.stats.MediaStatsView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ColorUtils.colorFromHex
import com.axiel7.anihyou.utils.ContextUtils.copyToClipBoard
import com.axiel7.anihyou.utils.ContextUtils.getCurrentLanguageTag
import com.axiel7.anihyou.utils.ContextUtils.openInGoogleTranslate
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.NumberUtils.format
import com.axiel7.anihyou.utils.StringUtils.htmlDecoded
import com.axiel7.anihyou.utils.StringUtils.htmlStripped
import com.axiel7.anihyou.utils.StringUtils.toAnnotatedString
import com.axiel7.anihyou.utils.UNKNOWN_CHAR
import kotlinx.coroutines.launch

const val MEDIA_ID_ARGUMENT = "{mediaId}"
const val MEDIA_DETAILS_DESTINATION = "media_details/$MEDIA_ID_ARGUMENT"

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
    navigateToFullscreenImage: (String) -> Unit,
    navigateToStudioDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    navigateToReviewDetails: (Int) -> Unit,
    navigateToThreadDetails: (Int) -> Unit,
    navigateToExplore: (mediaType: MediaType?, genre: String?, tag: String?) -> Unit,
) {
    val context = LocalContext.current
    val viewModel = viewModel { MediaDetailsViewModel(mediaId) }

    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    var isSynopsisExpanded by remember { mutableStateOf(false) }
    val maxLinesSynopsis by remember {
        derivedStateOf { if (isSynopsisExpanded) Int.MAX_VALUE else 5 }
    }
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
    val accessTokenPreference by rememberPreference(ACCESS_TOKEN_PREFERENCE_KEY, null)
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LaunchedEffect(mediaId) {
        viewModel.getDetails()
    }

    if (sheetState.isVisible && viewModel.mediaDetails != null) {
        EditMediaSheet(
            sheetState = sheetState,
            mediaDetails = viewModel.mediaDetails!!.basicMediaDetails,
            listEntry = viewModel.mediaDetails?.mediaListEntry?.basicMediaListEntry,
            bottomPadding = bottomBarPadding,
            onDismiss = { updatedListEntry ->
                scope.launch {
                    viewModel.onUpdateListEntry(updatedListEntry)
                    sheetState.hide()
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    BackIconButton(onClick = navigateBack)
                },
                actions = {
                    FavoriteIconButton(
                        isFavorite = viewModel.mediaDetails?.isFavourite ?: false,
                        onClick = {
                            scope.launch { viewModel.toggleFavorite() }
                        }
                    )
                    ShareIconButton(url = viewModel.mediaDetails?.siteUrl ?: "")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        floatingActionButton = {
            if (accessTokenPreference != null) {
                ExtendedFloatingActionButton(onClick = { scope.launch { sheetState.show() } }) {
                    Icon(
                        painter = painterResource(
                            if (isNewEntry) R.drawable.add_24
                            else R.drawable.edit_24
                        ),
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
                    viewModel.mediaDetails?.bannerImage?.let { navigateToFullscreenImage(it) }
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
                        .clickable {
                            viewModel.mediaDetails?.coverImage?.extraLarge?.let {
                                navigateToFullscreenImage(it)
                            }
                        }
                )
                // For some reason current material3 version applies an alpha to the icon tint
                // putting a surface seems to fix it
                Surface {
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
                            text = viewModel.mediaDetails?.basicMediaDetails?.durationText()
                                ?: UNKNOWN_CHAR,
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
                    }//:Column
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
                        text = stringResource(
                            R.string.episode_in_time,
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
                    text = viewModel.mediaDetails?.popularity?.format(),
                    subtitle = stringResource(R.string.popularity),
                    isLoading = viewModel.isLoading
                )
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(dividerHeight.dp)
                )
                TextSubtitleVertical(
                    text = viewModel.mediaDetails?.favourites?.format(),
                    subtitle = stringResource(R.string.favorites),
                    isLoading = viewModel.isLoading
                )
            }//: Row

            // Synopsis
            Text(
                text = viewModel.mediaDetails?.description?.htmlDecoded()?.toAnnotatedString()
                    ?: buildAnnotatedString {
                        if (viewModel.isLoading)
                            append(stringResource(R.string.lorem_ipsun))
                        else
                            append(stringResource(R.string.no_description))
                    },
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                    .clickable { isSynopsisExpanded = !isSynopsisExpanded }
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
                } else Spacer(modifier = Modifier.size(48.dp))

                IconButton(
                    onClick = { isSynopsisExpanded = !isSynopsisExpanded }
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
                    Icon(
                        painter = painterResource(R.drawable.content_copy_24),
                        contentDescription = "copy"
                    )
                }
            }//: Row

            // Genres
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp)
            ) {
                viewModel.mediaDetails?.genres?.forEach {
                    AssistChip(
                        onClick = {
                            navigateToExplore(
                                viewModel.mediaDetails?.basicMediaDetails?.type,
                                it,
                                null
                            )
                        },
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
                    modifier = Modifier.padding(horizontal = 16.dp),
                    defaultSelectedIndex = selectedTabIndex,
                    onItemSelection = {
                        selectedTabIndex = it
                    }
                )
                when (DetailsType.tabRows[selectedTabIndex].value) {
                    DetailsType.INFO ->
                        MediaInformationView(
                            viewModel = viewModel,
                            navigateToExplore = navigateToExplore,
                            navigateToStudioDetails = navigateToStudioDetails
                        )

                    DetailsType.STAFF_CHARACTERS ->
                        MediaCharacterStaffView(
                            viewModel = viewModel,
                            navigateToCharacterDetails = navigateToCharacterDetails,
                            navigateToStaffDetails = navigateToStaffDetails
                        )

                    DetailsType.RELATIONS ->
                        MediaRelationsView(
                            viewModel = viewModel,
                            navigateToDetails = navigateToMediaDetails
                        )

                    DetailsType.STATS -> MediaStatsView(
                        viewModel = viewModel
                    )

                    DetailsType.REVIEWS -> ReviewThreadListView(
                        viewModel = viewModel,
                        navigateToReviewDetails = navigateToReviewDetails,
                        navigateToThreadDetails = navigateToThreadDetails,
                    )
                }
            }//: Column
        }//: Column
    }//: Scaffold
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
            navigateToStudioDetails = {},
            navigateToCharacterDetails = {},
            navigateToStaffDetails = {},
            navigateToReviewDetails = {},
            navigateToThreadDetails = {},
            navigateToExplore = { _, _, _ -> }
        )
    }
}