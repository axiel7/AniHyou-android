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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.ui.screens.profile.stats.genres.GenresStatsView
import com.axiel7.anihyou.ui.screens.profile.stats.overview.OverviewStatsView
import com.axiel7.anihyou.ui.screens.profile.stats.staff.StaffStatsView
import com.axiel7.anihyou.ui.screens.profile.stats.tags.TagsStatsView
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun UserStatsView(
    userId: Int,
    modifier: Modifier = Modifier,
    nestedScrollConnection: NestedScrollConnection,
    navigateToGenreTag: (mediaType: MediaType, genre: String?, tag: String?) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
) {
    val viewModel: UserStatsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.setUserId(userId)
    }

    Column(
        modifier = modifier.fillMaxWidth()
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
                OverviewStatsView(
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
                    modifier = Modifier
                        .nestedScroll(nestedScrollConnection)
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                )
            }

            UserStatType.GENRES -> {
                GenresStatsView(
                    stats = if (uiState.mediaType == MediaType.ANIME) uiState.animeGenres
                    else uiState.mangaGenres,
                    isLoading = uiState.isLoading,
                    mediaType = uiState.mediaType,
                    setMediaType = viewModel::setMediaType,
                    genresType = uiState.genresType,
                    setGenresType = viewModel::setGenresType,
                    navigateToExplore = { genre ->
                        navigateToGenreTag(uiState.mediaType, genre, null)
                    },
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                )
            }

            UserStatType.TAGS -> {
                TagsStatsView(
                    stats = if (uiState.mediaType == MediaType.ANIME) uiState.animeTags
                    else uiState.mangaTags,
                    isLoading = uiState.isLoading,
                    mediaType = uiState.mediaType,
                    setMediaType = viewModel::setMediaType,
                    tagsType = uiState.tagsType,
                    setTagsType = viewModel::setTagsType,
                    navigateToExplore = { tag ->
                        navigateToGenreTag(uiState.mediaType, null, tag)
                    },
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                )
            }

            UserStatType.STAFF -> {
                StaffStatsView(
                    stats = if (uiState.mediaType == MediaType.ANIME) uiState.animeStaff
                    else uiState.mangaStaff,
                    isLoading = uiState.isLoading,
                    mediaType = uiState.mediaType,
                    setMediaType = viewModel::setMediaType,
                    staffType = uiState.staffType,
                    setStaffType = viewModel::setStaffType,
                    navigateToStaffDetails = navigateToStaffDetails,
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                )
            }

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
                userId = 1,
                navigateToGenreTag = { _, _, _ -> },
                navigateToStaffDetails = {},
                nestedScrollConnection = rememberNestedScrollInteropConnection(),
            )
        }
    }
}