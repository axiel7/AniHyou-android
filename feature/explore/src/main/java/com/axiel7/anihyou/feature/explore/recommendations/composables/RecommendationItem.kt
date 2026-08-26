package com.axiel7.anihyou.feature.explore.recommendations.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.network.MediaRecommendationsQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaFormat
import com.axiel7.anihyou.core.network.type.RecommendationRating
import com.axiel7.anihyou.core.ui.composables.UpvoteDownvoteHorizontalText
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun RecommendationItem(
    recommendation: MediaRecommendationsQuery.Recommendation,
    modifier: Modifier = Modifier,
    onClickMedia: (Int) -> Unit,
    onLongClickMedia: (BasicMediaDetails, BasicMediaListEntry?) -> Unit,
    onClickMediaRecommended: (Int) -> Unit,
    onVoteClick: (Int, Int, Int, RecommendationRating) -> Unit = { _, _, _, _ -> },
    blurAdult: Boolean,
) {
    val media = recommendation.media
    val mediaRecommended = recommendation.mediaRecommendation
    Card(
        modifier = modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            if (media != null && mediaRecommended != null) {
                MediaItemHorizontal(
                    title = media.basicMediaDetails.title?.userPreferred.orEmpty(),
                    imageUrl = media.coverImage?.large,
                    blurImage = blurAdult && media.basicMediaDetails.isAdult == true,
                    score = media.meanScore ?: 0,
                    format = media.format ?: MediaFormat.UNKNOWN__,
                    year = media.startDate?.year,
                    mediaStatus = media.status,
                    episodes = media.basicMediaDetails.episodes,
                    chapters = media.basicMediaDetails.chapters,
                    duration = media.basicMediaDetails.duration,
                    genres = media.genres?.filterNotNull(),
                    status = media.mediaListEntry?.basicMediaListEntry?.status,
                    onClick = { onClickMedia(media.basicMediaDetails.id) },
                    onLongClick = {
                        onLongClickMedia(
                            media.basicMediaDetails,
                            media.mediaListEntry?.basicMediaListEntry
                        )
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))
                MediaItemHorizontal(
                    title = mediaRecommended.basicMediaDetails.title?.userPreferred.orEmpty(),
                    imageUrl = mediaRecommended.coverImage?.large,
                    blurImage = blurAdult && mediaRecommended.basicMediaDetails.isAdult == true,
                    score = mediaRecommended.meanScore ?: 0,
                    format = mediaRecommended.format ?: MediaFormat.UNKNOWN__,
                    year = mediaRecommended.startDate?.year,
                    mediaStatus = mediaRecommended.status,
                    episodes = mediaRecommended.basicMediaDetails.episodes,
                    chapters = mediaRecommended.basicMediaDetails.chapters,
                    duration = mediaRecommended.basicMediaDetails.duration,
                    genres = mediaRecommended.genres?.filterNotNull(),
                    status = mediaRecommended.mediaListEntry?.basicMediaListEntry?.status,
                    onClick = { onClickMediaRecommended(mediaRecommended.basicMediaDetails.id) },
                    onLongClick = {
                        onLongClickMedia(
                            mediaRecommended.basicMediaDetails,
                            mediaRecommended.mediaListEntry?.basicMediaListEntry
                        )
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                val recMediaId = mediaRecommended.basicMediaDetails.id
                val mediaId = media.basicMediaDetails.id
                val recId = recommendation.id
                UpvoteDownvoteHorizontalText(
                    ratingText = recommendation.rating?.toString().orEmpty(),
                    isUpvoted = recommendation.userRating == RecommendationRating.RATE_UP,
                    isDownvoted = recommendation.userRating == RecommendationRating.RATE_DOWN,
                    onUpvoteClick = {
                        onVoteClick(recMediaId, mediaId, recId, RecommendationRating.RATE_UP)
                    },
                    onDownvoteClick = {
                        onVoteClick(recMediaId, mediaId, recId, RecommendationRating.RATE_DOWN)
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun RecommendationItemPlaceHolder() {
    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            MediaItemHorizontalPlaceholder()
            Spacer(modifier = Modifier.height(4.dp))
            MediaItemHorizontalPlaceholder()
            Spacer(modifier = Modifier.height(4.dp))
            UpvoteDownvoteHorizontalText(
                ratingText = "",
                isUpvoted = false,
                isDownvoted = false,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onUpvoteClick = {},
                onDownvoteClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun RecommendationItemPreview() {
    AniHyouTheme {
        Surface {
            //TODO: Make preview for RecommendationItem
        }
    }
}