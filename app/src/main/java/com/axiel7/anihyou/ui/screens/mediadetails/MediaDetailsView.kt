package com.axiel7.anihyou.ui.screens.mediadetails

import androidx.compose.animation.animateContentSize
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
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.genre.SelectableGenre.Companion.genreTagLocalized
import com.axiel7.anihyou.data.model.media.durationText
import com.axiel7.anihyou.data.model.media.isAnime
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.media.siteUrlWithTitle
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.ui.composables.TopBannerView
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.common.ShareIconButton
import com.axiel7.anihyou.ui.composables.common.TranslateIconButton
import com.axiel7.anihyou.ui.composables.common.singleClick
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_BIG_HEIGHT
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_BIG_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaPoster
import com.axiel7.anihyou.ui.screens.characterdetails.composables.CharacterVoiceActorsSheet
import com.axiel7.anihyou.ui.screens.mediadetails.composables.MediaCharacterStaffView
import com.axiel7.anihyou.ui.screens.mediadetails.composables.MediaInformationView
import com.axiel7.anihyou.ui.screens.mediadetails.composables.MediaRelationsView
import com.axiel7.anihyou.ui.screens.mediadetails.composables.MediaStatsView
import com.axiel7.anihyou.ui.screens.mediadetails.composables.ReviewThreadListView
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ColorUtils.colorFromHex
import com.axiel7.anihyou.utils.ContextUtils.copyToClipBoard
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.LocaleUtils.LocalIsLanguageEn
import com.axiel7.anihyou.utils.NumberUtils.format
import com.axiel7.anihyou.utils.StringUtils.htmlDecoded
import com.axiel7.anihyou.utils.StringUtils.htmlStripped
import com.axiel7.anihyou.utils.StringUtils.orUnknown
import com.axiel7.anihyou.utils.StringUtils.toAnnotatedString
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MediaDetails(val id: Int)

@Composable
fun MediaDetailsView(
    isLoggedIn: Boolean,
    navActionManager: NavActionManager,
) {
    val viewModel: MediaDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(isLoggedIn) {
        viewModel.setIsLoggedIn(isLoggedIn)
    }

    MediaDetailsContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun MediaDetailsContent(
    uiState: MediaDetailsUiState,
    event: MediaDetailsEvent?,
    navActionManager: NavActionManager,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    val isTopAppBarScrolled by remember {
        derivedStateOf { topAppBarScrollBehavior.state.overlappedFraction == 1f }
    }
    var showEditSheet by remember { mutableStateOf(false) }

    var isSynopsisExpanded by remember { mutableStateOf(false) }
    val maxLinesSynopsis by remember {
        derivedStateOf { if (isSynopsisExpanded) Int.MAX_VALUE else 5 }
    }
    val iconExpand by remember {
        derivedStateOf {
            if (isSynopsisExpanded) R.drawable.expand_less_24
            else R.drawable.expand_more_24
        }
    }
    val isCurrentLanguageEn = LocalIsLanguageEn.current
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    if (showEditSheet && uiState.details != null) {
        EditMediaSheet(
            mediaDetails = uiState.details.basicMediaDetails,
            listEntry = uiState.details.mediaListEntry?.basicMediaListEntry,
            bottomPadding = bottomBarPadding,
            scope = scope,
            onEntryUpdated = {
                event?.onUpdateListEntry(it)
            },
            onDismissed = { showEditSheet = false }
        )
    }

    if (uiState.showVoiceActorsSheet) {
        CharacterVoiceActorsSheet(
            voiceActors = uiState.selectedCharacterVoiceActors.orEmpty(),
            scope = scope,
            navigateToStaffDetails = {
                event?.hideVoiceActorSheet()
                navActionManager.toStaffDetails(it)
            },
            onDismiss = {
                event?.hideVoiceActorSheet()
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    if (isTopAppBarScrolled) {
                        Text(
                            text = uiState.details?.title?.userPreferred.orEmpty(),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                    }
                },
                navigationIcon = {
                    BackIconButton(onClick = navActionManager::goBack)
                },
                actions = {
                    if (uiState.isLoggedIn) {
                        FavoriteIconButton(
                            isFavorite = uiState.details?.isFavourite == true,
                            onClick = {
                                event?.toggleFavorite()
                            }
                        )
                    }
                    ShareIconButton(
                        url = { uiState.details?.siteUrlWithTitle().orEmpty() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        floatingActionButton = {
            if (uiState.isLoggedIn) {
                ExtendedFloatingActionButton(onClick = { showEditSheet = true }) {
                    Icon(
                        painter = painterResource(
                            if (uiState.isNewEntry) R.drawable.add_24
                            else R.drawable.edit_24
                        ),
                        contentDescription = stringResource(R.string.edit)
                    )
                    Text(
                        text = if (uiState.isNewEntry) stringResource(R.string.add)
                        else uiState.details?.mediaListEntry?.basicMediaListEntry?.status?.localized(
                            mediaType = uiState.details.basicMediaDetails.type
                                ?: MediaType.UNKNOWN__
                        ) ?: stringResource(R.string.edit),
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
                imageUrl = uiState.details?.bannerImage,
                modifier = Modifier.clickable {
                    uiState.details?.bannerImage?.let(navActionManager::toFullscreenImage)
                },
                fallbackColor = colorFromHex(uiState.details?.coverImage?.color),
                height = padding.calculateTopPadding() + 80.dp
            )

            // Poster and basic info
            Row {
                MediaPoster(
                    url = uiState.details?.coverImage?.large,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .size(
                            width = MEDIA_POSTER_BIG_WIDTH.dp,
                            height = MEDIA_POSTER_BIG_HEIGHT.dp
                        )
                        .clickable(onClick = singleClick {
                            uiState.details?.coverImage?.extraLarge
                                ?.let(navActionManager::toFullscreenImage)
                        })
                )
                Column {
                    Text(
                        text = uiState.details?.title?.userPreferred
                            ?: stringResource(R.string.unknown),
                        modifier = Modifier
                            .padding(bottom = 8.dp, end = 8.dp)
                            .defaultPlaceholder(visible = uiState.isLoading)
                            .combinedClickable(
                                onLongClick = {
                                    uiState.details?.title?.userPreferred
                                        ?.let { context.copyToClipBoard(it) }
                                },
                                onClick = { }
                            ),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextIconHorizontal(
                        text = uiState.details?.format?.localized()
                            ?: stringResource(R.string.unknown),
                        icon = if (uiState.details?.basicMediaDetails?.isAnime() == true)
                            R.drawable.live_tv_24
                        else R.drawable.book_24,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .defaultPlaceholder(visible = uiState.isLoading)
                    )
                    TextIconHorizontal(
                        text = uiState.details?.basicMediaDetails?.durationText()
                            ?: stringResource(R.string.unknown),
                        icon = R.drawable.timer_24,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .defaultPlaceholder(visible = uiState.isLoading)
                    )
                    TextIconHorizontal(
                        text = uiState.details?.status.localized(),
                        icon = R.drawable.rss_feed_24,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .defaultPlaceholder(visible = uiState.isLoading)
                    )
                }//:Column
            }//:Row

            // General info
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                val dividerHeight = 36
                uiState.details?.nextAiringEpisode?.let { nextAiringEpisode ->
                    TextSubtitleVertical(
                        text = stringResource(
                            R.string.episode_in_time,
                            nextAiringEpisode.episode,
                            nextAiringEpisode.timeUntilAiring.toLong().secondsToLegibleText()
                        ),
                        subtitle = stringResource(R.string.airing),
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .height(dividerHeight.dp)
                    )
                }
                TextSubtitleVertical(
                    text = "${uiState.details?.meanScore?.format().orUnknown()}%",
                    subtitle = stringResource(R.string.mean_score),
                    isLoading = uiState.isLoading
                )
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(dividerHeight.dp)
                )
                TextSubtitleVertical(
                    text = "${uiState.details?.averageScore?.format().orUnknown()}%",
                    subtitle = stringResource(R.string.average_score),
                    isLoading = uiState.isLoading
                )
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(dividerHeight.dp)
                )
                TextSubtitleVertical(
                    text = uiState.details?.popularity?.format(),
                    subtitle = stringResource(R.string.popularity),
                    isLoading = uiState.isLoading
                )
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .height(dividerHeight.dp)
                )
                TextSubtitleVertical(
                    text = uiState.details?.favourites?.format(),
                    subtitle = stringResource(R.string.favorites),
                    isLoading = uiState.isLoading
                )
            }//: Row

            // Synopsis
            Text(
                text = when {
                    uiState.isLoading -> buildAnnotatedString {
                        append(stringResource(R.string.lorem_ipsun))
                    }

                    uiState.details?.description.isNullOrBlank() -> buildAnnotatedString {
                        append(stringResource(R.string.no_description))
                    }

                    else -> uiState.details.description.htmlDecoded().toAnnotatedString()
                },
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                    .clickable { isSynopsisExpanded = !isSynopsisExpanded }
                    .animateContentSize()
                    .defaultPlaceholder(visible = uiState.isLoading),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLinesSynopsis
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isCurrentLanguageEn) {
                    TranslateIconButton(
                        text = uiState.details?.description?.htmlStripped()
                    )
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                IconButton(
                    onClick = { isSynopsisExpanded = !isSynopsisExpanded }
                ) {
                    Icon(
                        painter = painterResource(iconExpand),
                        contentDescription = stringResource(R.string.expand)
                    )
                }

                IconButton(
                    onClick = {
                        uiState.details?.description?.let {
                            context.copyToClipBoard(it.htmlStripped())
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.content_copy_24),
                        contentDescription = stringResource(R.string.copy)
                    )
                }
            }//: Row

            // Genres
            Row(
                modifier = Modifier
                    .height(32.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp)
            ) {
                uiState.details?.genres?.filterNotNull()?.forEach { genre ->
                    AssistChip(
                        onClick = {
                            uiState.details.basicMediaDetails.type?.let { mediaType ->
                                navActionManager.toGenreTag(mediaType, genre, null)
                            }
                        },
                        label = { Text(text = genre.genreTagLocalized()) },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Other info
            MediaInfoTabs(
                event = event,
                uiState = uiState,
                navActionManager = navActionManager,
            )
        }//: Column
    }//: Scaffold
}

@Composable
fun MediaInfoTabs(
    event: MediaDetailsEvent?,
    uiState: MediaDetailsUiState,
    navActionManager: NavActionManager,
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SegmentedButtons(
            items = MediaDetailsType.tabRows,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            selectedIndex = selectedTabIndex,
            onItemSelection = {
                selectedTabIndex = it
            }
        )
        when (MediaDetailsType.tabRows[selectedTabIndex].value) {
            MediaDetailsType.INFO ->
                MediaInformationView(
                    uiState = uiState,
                    navigateToGenreTag = navActionManager::toGenreTag,
                    navigateToStudioDetails = navActionManager::toStudioDetails,
                    navigateToAnimeSeason = navActionManager::toAnimeSeason
                )

            MediaDetailsType.STAFF_CHARACTERS ->
                MediaCharacterStaffView(
                    uiState = uiState,
                    fetchData = { event?.fetchCharactersAndStaff() },
                    navigateToCharacterDetails = navActionManager::toCharacterDetails,
                    navigateToStaffDetails = navActionManager::toStaffDetails,
                    showVoiceActorsSheet = {
                        event?.showVoiceActorsSheet(it)
                    }
                )

            MediaDetailsType.RELATIONS ->
                MediaRelationsView(
                    uiState = uiState,
                    fetchData = { event?.fetchRelationsAndRecommendations() },
                    navigateToDetails = navActionManager::toMediaDetails
                )

            MediaDetailsType.STATS ->
                MediaStatsView(
                    uiState = uiState,
                    fetchData = { event?.fetchStats() },
                    navigateToUserDetails = navActionManager::toUserDetails
                )

            MediaDetailsType.REVIEWS -> {
                LaunchedEffect(uiState.threads, uiState.reviews) {
                    if (uiState.threads.isEmpty() && uiState.reviews.isEmpty()) {
                        event?.fetchThreads()
                        event?.fetchReviews()
                        event?.fetchActivity()
                    }
                }
                ReviewThreadListView(
                    uiState = uiState,
                    navActionManager = navActionManager,
                )
            }
        }
    }//: Column
}

@Preview
@Composable
fun MediaDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            MediaDetailsContent(
                uiState = MediaDetailsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}