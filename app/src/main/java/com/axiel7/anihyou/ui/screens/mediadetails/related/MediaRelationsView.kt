package com.axiel7.anihyou.ui.screens.mediadetails.related

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.screens.home.HomeLazyRow
import com.axiel7.anihyou.ui.screens.mediadetails.MediaDetailsViewModel
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.toStringOrZero

@Composable
fun MediaRelationsView(
    viewModel: MediaDetailsViewModel,
    navigateToDetails: (Int) -> Unit,
) {
    val relationsAndRecommendations by viewModel.relationsAndRecommendations.collectAsState()
    val isLoading by remember {
        derivedStateOf { relationsAndRecommendations is DataResult.Loading }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Related
        val mediaRelated by remember {
            derivedStateOf {
                (relationsAndRecommendations as? DataResult.Success)?.data?.relations.orEmpty()
            }
        }
        if (isLoading || mediaRelated.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.related))
            HomeLazyRow {
                if (isLoading) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
                else items(mediaRelated) { item ->
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
        val mediaRecommendations by remember {
            derivedStateOf {
                (relationsAndRecommendations as? DataResult.Success)?.data?.recommendations.orEmpty()
            }
        }
        if (isLoading || mediaRecommendations.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.recommendations))
            HomeLazyRow {
                if (isLoading) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
                else items(mediaRecommendations) { item ->
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
                viewModel = MediaDetailsViewModel(mediaId = 1),
                navigateToDetails = {}
            )
        }
    }
}