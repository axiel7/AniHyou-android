package com.axiel7.anihyou.ui.mediadetails.related

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.home.HomeLazyRow
import com.axiel7.anihyou.ui.mediadetails.MediaDetailsViewModel
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.toStringOrZero

@Composable
fun MediaRelationsView(
    mediaId: Int,
    viewModel: MediaDetailsViewModel,
    navigateToDetails: (Int) -> Unit,
) {
    LaunchedEffect(mediaId) {
        if (viewModel.mediaRelated.isEmpty())
            viewModel.getMediaRelationsRecommendations(mediaId)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Related
        if (viewModel.isLoadingRelationsRecommendations || viewModel.mediaRelated.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.related))
            HomeLazyRow {
                if (viewModel.isLoadingRelationsRecommendations) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
                else items(viewModel.mediaRelated) { item ->
                    MediaItemVertical(
                        title = item.mediaRelated.node?.title?.userPreferred ?: "",
                        imageUrl = item.mediaRelated.node?.coverImage?.large,
                        modifier = Modifier.padding(start = 8.dp),
                        subtitle = {
                            Text(
                                text = item.mediaRelated.relationType?.localized() ?: "",
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 15.sp,
                            )
                        },
                        onClick = {
                            navigateToDetails(item.mediaRelated.node?.id!!)
                        }
                    )
                }
            }
        }

        // Recommendations
        if (viewModel.isLoadingRelationsRecommendations || viewModel.mediaRecommendations.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.recommendations))
            HomeLazyRow {
                if (viewModel.isLoadingRelationsRecommendations) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
                else items(viewModel.mediaRecommendations) { item ->
                    MediaItemVertical(
                        title = item.mediaRecommended.mediaRecommendation?.title?.userPreferred ?: "",
                        imageUrl = item.mediaRecommended.mediaRecommendation?.coverImage?.large,
                        modifier = Modifier.padding(start = 8.dp),
                        subtitle = {
                            TextIconHorizontal(
                                text = item.mediaRecommended.rating.toStringOrZero(),
                                icon = R.drawable.thumbs_up_down_20,
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 14.sp
                            )
                        },
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
                mediaId = 1,
                viewModel = viewModel(),
                navigateToDetails = {}
            )
        }
    }
}