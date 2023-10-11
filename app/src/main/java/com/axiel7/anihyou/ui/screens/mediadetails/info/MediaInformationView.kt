package com.axiel7.anihyou.ui.screens.mediadetails.info

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.displayName
import com.axiel7.anihyou.data.model.media.externalLinks
import com.axiel7.anihyou.data.model.media.isAnime
import com.axiel7.anihyou.data.model.media.link
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.media.seasonAndYear
import com.axiel7.anihyou.data.model.media.streamingLinks
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.InfoClickableItemView
import com.axiel7.anihyou.ui.composables.InfoItemView
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.SpoilerTagChip
import com.axiel7.anihyou.ui.composables.TagChip
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.media.VIDEO_SMALL_WIDTH
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
    navigateToExplore: (mediaType: MediaType?, genre: String?, tag: String?) -> Unit,
    navigateToStudioDetails: (Int) -> Unit,
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
                info = uiState.details?.seasonAndYear()
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
        InfoItemView(
            title = "Synonyms",
            info = uiState.details?.synonyms?.joinToString("\n"),
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
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
                TextButton(onClick = { showSpoiler = !showSpoiler }) {
                    Text(
                        text = stringResource(
                            if (showSpoiler) R.string.hide_spoiler else R.string.show_spoiler
                        )
                    )
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
                                navigateToExplore(
                                    uiState.details.basicMediaDetails.type,
                                    null,
                                    tag.name
                                )
                            }
                        )
                    } else {
                        SpoilerTagChip(
                            name = tag.name,
                            description = tag.description,
                            rank = tag.rank,
                            visible = showSpoiler,
                            onClick = {
                                navigateToExplore(
                                    uiState.details.basicMediaDetails.type,
                                    null,
                                    tag.name
                                )
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
            InfoTitle(text = stringResource(R.string.episodes))
            LazyRow(
                modifier = Modifier.padding(bottom = 4.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(uiState.details!!.streamingEpisodes!!) { item ->
                    Column {
                        VideoThumbnailItem(
                            imageUrl = item?.thumbnail,
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                item?.url?.let { context.openActionView(it) }
                            }
                        )
                        Text(
                            text = item?.title ?: "",
                            modifier = Modifier
                                .width(VIDEO_SMALL_WIDTH.dp)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 15.sp,
                            lineHeight = 18.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Streaming links
        uiState.details?.streamingLinks()?.let { streamingLinks ->
            if (streamingLinks.isNotEmpty()) {
                InfoTitle(text = stringResource(R.string.streaming_sites))
                FlowRow(
                    modifier = Modifier.padding(8.dp)
                ) {
                    streamingLinks.forEach { link ->
                        TextButton(onClick = { link.url?.let { context.openActionView(it) } }) {
                            Text(text = link.displayName())
                        }
                    }
                }
            }
        }

        //External links
        uiState.details?.externalLinks()?.let { externalLinks ->
            if (externalLinks.isNotEmpty()) {
                InfoTitle(text = stringResource(R.string.external_links))
                FlowRow(
                    modifier = Modifier.padding(8.dp)
                ) {
                    externalLinks.forEach { link ->
                        TextButton(onClick = { link.url?.let { context.openActionView(it) } }) {
                            Text(text = link.displayName())
                        }
                    }
                }
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
                navigateToExplore = { _, _, _ -> },
                navigateToStudioDetails = {}
            )
        }
    }
}