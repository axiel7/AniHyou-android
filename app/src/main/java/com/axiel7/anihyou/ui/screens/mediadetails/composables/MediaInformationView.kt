package com.axiel7.anihyou.ui.screens.mediadetails.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.episodeNumber
import com.axiel7.anihyou.data.model.media.externalLinks
import com.axiel7.anihyou.data.model.media.isAnime
import com.axiel7.anihyou.data.model.media.languageShort
import com.axiel7.anihyou.data.model.media.link
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.media.seasonAndYear
import com.axiel7.anihyou.data.model.media.streamingLinks
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.InfoClickableItemView
import com.axiel7.anihyou.ui.composables.InfoItemView
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.common.SpoilerTagChip
import com.axiel7.anihyou.ui.composables.common.TagChip
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.media.VideoThumbnailItem
import com.axiel7.anihyou.ui.screens.mediadetails.MediaDetailsUiState
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.openActionView
import com.axiel7.anihyou.utils.DateUtils.formatted
import com.axiel7.anihyou.utils.DateUtils.minutesToLegibleText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaInformationView(
    uiState: MediaDetailsUiState,
    navigateToGenreTag: (mediaType: MediaType, genre: String?, tag: String?) -> Unit,
    navigateToStudioDetails: (Int) -> Unit,
    navigateToAnimeSeason: (AnimeSeason) -> Unit,
) {
    val context = LocalContext.current
    var showSpoiler by remember { mutableStateOf(false) }
    val isAnime = uiState.details?.basicMediaDetails?.isAnime() == true

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        InfoTitle(text = stringResource(R.string.information))

        InfoItemView(
            title = stringResource(R.string.duration),
            info = uiState.details?.duration?.toLong()?.minutesToLegibleText(),
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.start_date),
            info = uiState.details?.startDate?.fuzzyDate?.formatted(),
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.end_date),
            info = uiState.details?.endDate?.fuzzyDate?.formatted(),
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        if (isAnime) {
            InfoItemView(
                title = stringResource(R.string.season),
                info = uiState.details?.seasonAndYear(),
                modifier = Modifier.clickable {
                    uiState.details?.season?.let { season ->
                        uiState.details.seasonYear?.let { year ->
                            navigateToAnimeSeason(AnimeSeason(year, season))
                        }
                    }
                }
            )
        }
        InfoItemView(
            title = stringResource(R.string.source),
            info = uiState.details?.source?.localized(),
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.romaji),
            info = uiState.details?.title?.romaji,
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.english),
            info = uiState.details?.title?.english,
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.native_title),
            info = uiState.details?.title?.native,
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        if (!uiState.details?.synonyms.isNullOrEmpty()) {
            InfoItemView(
                title = stringResource(R.string.synonyms),
                info = uiState.details?.synonyms?.joinToString("\n"),
                modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
            )
        }
        if (isAnime) {
            InfoClickableItemView(
                title = stringResource(R.string.studios),
                items = uiState.studios.orEmpty(),
                itemName = { it.name },
                onItemClicked = {
                    navigateToStudioDetails(it.id)
                }
            )
            InfoClickableItemView(
                title = stringResource(R.string.producers),
                items = uiState.producers.orEmpty(),
                itemName = { it.name },
                onItemClicked = {
                    navigateToStudioDetails(it.id)
                }
            )
        }

        // Tags
        InfoTitle(
            text = stringResource(R.string.tags),
            trailingIcon = {
                if (uiState.hasSpoilerTags) {
                    TextButton(onClick = { showSpoiler = !showSpoiler }) {
                        Text(
                            text = stringResource(
                                if (showSpoiler) R.string.hide_spoiler else R.string.show_spoiler
                            )
                        )
                    }
                }
            }
        )
        FlowRow(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .animateContentSize()
        ) {
            uiState.details?.tags?.forEach { tag ->
                if (tag != null) {
                    if (tag.isMediaSpoiler == false) {
                        TagChip(
                            name = tag.name,
                            description = tag.description,
                            rank = tag.rank,
                            onClick = {
                                uiState.details.basicMediaDetails.type?.let { mediaType ->
                                    navigateToGenreTag(mediaType, null, tag.name)
                                }
                            }
                        )
                    } else {
                        SpoilerTagChip(
                            name = tag.name,
                            description = tag.description,
                            rank = tag.rank,
                            visible = showSpoiler,
                            onClick = {
                                uiState.details.basicMediaDetails.type?.let { mediaType ->
                                    navigateToGenreTag(mediaType, null, tag.name)
                                }
                            }
                        )
                    }
                }
            }
        }//: FlowRow

        // Trailer
        uiState.details?.trailer?.let { trailer ->
            InfoTitle(text = stringResource(R.string.trailer))
            VideoThumbnailItem(
                imageUrl = trailer.thumbnail,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = {
                    trailer.link()?.let { context.openActionView(it) }
                }
            )
        }

        // Streaming episodes
        if (!uiState.details?.streamingEpisodes.isNullOrEmpty()) {
            // Next episode to watch
            uiState.details?.mediaListEntry?.basicMediaListEntry?.progress?.let { progress ->
                if (progress > 0) {
                    uiState.details.streamingEpisodes
                        ?.find { it?.episodeNumber() == progress + 1 }
                        ?.let { nextEpisode ->
                            InfoTitle(text = stringResource(R.string.continue_watching))
                            EpisodeItem(
                                item = nextEpisode,
                                modifier = Modifier.padding(start = 8.dp),
                                onClick = {
                                    nextEpisode.url?.let { context.openActionView(it) }
                                }
                            )
                        }
                }
            }
            InfoTitle(text = stringResource(R.string.episodes))
            LazyRow(
                modifier = Modifier.padding(bottom = 4.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(uiState.details?.streamingEpisodes.orEmpty()) { item ->
                    EpisodeItem(
                        item = item,
                        onClick = {
                            item?.url?.let { context.openActionView(it) }
                        }
                    )
                }
            }
        }

        // Streaming links
        uiState.details?.streamingLinks()?.let { streamingLinks ->
            if (streamingLinks.isNotEmpty()) {
                InfoTitle(text = stringResource(R.string.streaming_sites))
                FlowRow(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp
                    )
                ) {
                    streamingLinks.forEach { link ->
                        AssistChip(
                            onClick = { link.url?.let { context.openActionView(it) } },
                            label = { Text(text = link.site) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            trailingIcon = {
                                link.languageShort()?.let { lang ->
                                    Text(text = lang)
                                }
                            }
                        )
                    }
                }
            }
        }

        // External links
        uiState.details?.externalLinks()?.let { externalLinks ->
            if (externalLinks.isNotEmpty()) {
                InfoTitle(text = stringResource(R.string.external_links))
                FlowRow(
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp
                    )
                ) {
                    externalLinks.forEach { link ->
                        AssistChip(
                            onClick = { link.url?.let { context.openActionView(it) } },
                            label = { Text(text = link.site) },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            trailingIcon = {
                                link.languageShort()?.let { lang ->
                                    Text(text = lang)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Openings/Endings
        var showMusicSheet by remember { mutableStateOf(false) }
        var selectedSong by remember { mutableStateOf<String?>(null) }

        if (showMusicSheet && selectedSong != null) {
            MusicStreamingSheet(
                songTitle = selectedSong.orEmpty(),
                bottomPadding = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding(),
                onDismiss = {
                    showMusicSheet = false
                    selectedSong = null
                }
            )
        }
        if (!uiState.openings.isNullOrEmpty()) {
            InfoTitle(text = stringResource(R.string.openings))

            uiState.openings.forEach { theme ->
                Text(
                    text = theme.text,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable {
                            selectedSong = theme.text
                            showMusicSheet = true
                        },
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        if (!uiState.endings.isNullOrEmpty()) {
            InfoTitle(text = stringResource(R.string.endings))

            uiState.endings.forEach { theme ->
                Text(
                    text = theme.text,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable {
                            selectedSong = theme.text
                            showMusicSheet = true
                        },
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }//: Column
}

@Preview
@Composable
fun MediaInformationViewPreview() {
    AniHyouTheme {
        Surface {
            MediaInformationView(
                uiState = MediaDetailsUiState(),
                navigateToGenreTag = { _, _, _ -> },
                navigateToStudioDetails = {},
                navigateToAnimeSeason = {}
            )
        }
    }
}