package com.axiel7.anihyou.feature.mediadetails.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.InfoTitle
import com.axiel7.anihyou.core.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.core.ui.composables.list.DiscoverLazyRow
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.mediadetails.MediaDetailsUiState

@Composable
fun MediaRelationsView(
    uiState: MediaDetailsUiState,
    fetchData: () -> Unit,
    navigateToDetails: (Int) -> Unit,
) {
    val isLoading = uiState.relationsAndRecommendations == null

    LaunchedEffect(uiState.relationsAndRecommendations) {
        if (uiState.relationsAndRecommendations == null) fetchData()
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Related
        val mediaRelated = uiState.relationsAndRecommendations?.relations.orEmpty()
        if (isLoading || mediaRelated.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.related))
            DiscoverLazyRow {
                if (isLoading) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
                items(
                    items = mediaRelated,
                    contentType = { it }
                ) { item ->
                    MediaItemVertical(
                        title = item.mediaRelated.node?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                        imageUrl = item.mediaRelated.node?.coverImage?.large,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        subtitle = {
                            Text(
                                text = item.mediaRelated.relationType?.localized().orEmpty(),
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 15.sp,
                            )
                        },
                        status = item.mediaRelated.node?.mediaListEntry?.basicMediaListEntry?.status,
                        minLines = 2,
                        onClick = {
                            navigateToDetails(item.mediaRelated.node?.id ?: 0)
                        }
                    )
                }
            }
        }

        // Recommendations
        val mediaRecommendations = uiState.relationsAndRecommendations?.recommendations.orEmpty()
        if (isLoading || mediaRecommendations.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.recommendations))
            DiscoverLazyRow {
                if (isLoading) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
                items(
                    count = mediaRecommendations.size,
                    contentType = { it }
                ) {
                    val item = mediaRecommendations[it]
                    MediaItemVertical(
                        title = item.mediaRecommended.mediaRecommendation?.basicMediaDetails
                            ?.title?.userPreferred.orEmpty(),
                        imageUrl = item.mediaRecommended.mediaRecommendation?.coverImage?.large,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        subtitle = {
                            TextIconHorizontal(
                                text = item.mediaRecommended.rating?.format().orEmpty(),
                                icon = R.drawable.thumbs_up_down_20,
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 14.sp
                            )
                        },
                        status = item.mediaRecommended.mediaRecommendation?.mediaListEntry
                            ?.basicMediaListEntry?.status,
                        minLines = 2,
                        onClick = {
                            navigateToDetails(item.mediaRecommended.mediaRecommendation?.id!!)
                        }
                    )
                }
            }
        }
    }//: Column
}

@Preview
@Composable
fun MediaRelationsViewPreview() {
    AniHyouTheme {
        Surface {
            MediaRelationsView(
                uiState = MediaDetailsUiState(),
                fetchData = {},
                navigateToDetails = {}
            )
        }
    }
}