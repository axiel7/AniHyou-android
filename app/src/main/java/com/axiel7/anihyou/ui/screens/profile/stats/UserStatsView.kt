package com.axiel7.anihyou.ui.screens.profile.stats

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun UserStatsView(
    userId: Int,
    modifier: Modifier = Modifier
) {
    val viewModel: UserStatsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.setUserId(userId)
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            UserStatType.entries.forEach {
                FilterSelectionChip(
                    selected = uiState.type == it,
                    text = it.localized(),
                    onClick = {
                        viewModel.setType(it)
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }//: Row

        when (uiState.type) {
            UserStatType.OVERVIEW -> {
                OverviewUserStatsView(
                    stats = if (uiState.mediaType == MediaType.ANIME) uiState.animeOverview
                    else uiState.mangaOverview,
                    isLoading = uiState.isLoading,
                    mediaType = uiState.mediaType,
                    setMediaType = viewModel::setMediaType,
                    scoreType = uiState.scoreType,
                    setScoreType = viewModel::setScoreType,
                    lengthType = uiState.lengthType,
                    setLengthType = viewModel::setLengthType,
                    releaseYearType = uiState.releaseYearType,
                    setReleaseYearType = viewModel::setReleaseYearType,
                    startYearType = uiState.startYearType,
                    setStartYearType = viewModel::setStartYearType,
                )
            }

            UserStatType.GENRES -> ComingSoonText()
            UserStatType.TAGS -> ComingSoonText()
            UserStatType.STAFF -> ComingSoonText()
            UserStatType.VOICE_ACTORS -> ComingSoonText()
            UserStatType.STUDIOS -> ComingSoonText()
        }
    }//: Column
}

@Composable
fun ComingSoonText() {
    Text(
        text = "Coming Soon",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
fun UserStatsViewPreview() {
    AniHyouTheme {
        Surface {
            UserStatsView(
                userId = 1
            )
        }
    }
}