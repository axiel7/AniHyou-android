package com.axiel7.anihyou.feature.mediadetails.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.model.media.color
import com.axiel7.anihyou.core.model.media.icon
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaRankType
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.InfoTitle
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.stats.HorizontalStatsBar
import com.axiel7.anihyou.core.ui.composables.stats.VerticalStatsBar
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.mediadetails.MediaDetailsUiState

@Composable
fun MediaStatsView(
    uiState: MediaDetailsUiState,
    fetchData: () -> Unit,
    navigateToUserDetails: (Int) -> Unit,
) {
    val isLoading = !uiState.isSuccessStats
    LaunchedEffect(uiState.isSuccessStats) {
        if (!uiState.isSuccessStats) fetchData()
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Rankings
        if (isLoading || uiState.mediaRankings.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.rankings))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                uiState.mediaRankings.forEach {
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                text = buildString {
                                    append("#${it.rank.format()} ${it.context.capitalize(Locale.current)}")
                                    it.season?.let { season ->
                                        append(" ${season.localized()}")
                                    }
                                    it.year?.let { year ->
                                        append(" $year")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(it.type.icon()),
                                contentDescription = it.type.name,
                                modifier = if (it.type == MediaRankType.POPULAR)
                                    Modifier.padding(start = 2.dp)
                                else Modifier,
                                tint = it.type.color()
                            )
                        },
                        trailingIcon = {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                    )
                }
                if (isLoading) {
                    for (i in 1..3) {
                        Text(
                            text = "This is a loading placeholder",
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .defaultPlaceholder(visible = true)
                        )
                    }
                }
            }//: Column
        }

        // Status distribution
        InfoTitle(text = stringResource(R.string.status_distribution))
        HorizontalStatsBar(
            stats = uiState.mediaStatusDistribution,
            horizontalPadding = 16.dp,
            isLoading = isLoading
        )

        // Score distribution
        InfoTitle(text = stringResource(R.string.score_distribution))
        VerticalStatsBar(
            stats = uiState.mediaScoreDistribution,
            modifier = Modifier.padding(8.dp),
            isLoading = isLoading
        )

        // Following
        if (uiState.following.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.following))
            uiState.following.forEach { item ->
                FollowingUserItem(
                    mediaType = uiState.details?.basicMediaDetails?.type ?: MediaType.UNKNOWN__,
                    avatarUrl = item.user?.avatar?.medium,
                    username = item.user?.name.orEmpty(),
                    status = item.status ?: MediaListStatus.UNKNOWN__,
                    score = item.score,
                    scoreFormat = item.user?.mediaListOptions?.scoreFormat,
                    modifier = Modifier.clickable {
                        item.user?.id?.let(navigateToUserDetails)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun MediaStatsViewPreview() {
    AniHyouTheme {
        Surface {
            MediaStatsView(
                uiState = MediaDetailsUiState(),
                fetchData = {},
                navigateToUserDetails = {}
            )
        }
    }
}