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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.ui.screens.profile.stats.genres.GenresStatsView
import com.axiel7.anihyou.ui.screens.profile.stats.overview.OverviewStatsView
import com.axiel7.anihyou.ui.screens.profile.stats.staff.StaffStatsView
import com.axiel7.anihyou.ui.screens.profile.stats.studios.StudiosStatsView
import com.axiel7.anihyou.ui.screens.profile.stats.tags.TagsStatsView
import com.axiel7.anihyou.ui.screens.profile.stats.voiceactors.VoiceActorsStatsView
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun UserStatsView(
    userId: Int,
    modifier: Modifier = Modifier,
    nestedScrollConnection: NestedScrollConnection,
    navActionManager: NavActionManager,
) {
    val viewModel: UserStatsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.setUserId(userId)
    }

    UserStatsContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
        modifier = modifier,
        nestedScrollConnection = nestedScrollConnection
    )
}

@Composable
private fun UserStatsContent(
    uiState: UserStatsUiState,
    event: UserStatsEvent?,
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier,
    nestedScrollConnection: NestedScrollConnection,
) {
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
                        event?.setType(it)
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }//: Row

        when (uiState.type) {
            UserStatType.OVERVIEW -> {
                OverviewStatsView(
                    uiState = uiState,
                    event = event,
                    modifier = Modifier
                        .nestedScroll(nestedScrollConnection)
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                )
            }

            UserStatType.GENRES -> {
                GenresStatsView(
                    uiState = uiState,
                    event = event,
                    navActionManager = navActionManager,
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                )
            }

            UserStatType.TAGS -> {
                TagsStatsView(
                    uiState = uiState,
                    event = event,
                    navActionManager = navActionManager,
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                )
            }

            UserStatType.STAFF -> {
                StaffStatsView(
                    uiState = uiState,
                    event = event,
                    navActionManager = navActionManager,
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                )
            }

            UserStatType.VOICE_ACTORS -> {
                VoiceActorsStatsView(
                    uiState = uiState,
                    event = event,
                    navActionManager = navActionManager,
                )
            }

            UserStatType.STUDIOS -> {
                StudiosStatsView(
                    uiState = uiState,
                    event = event,
                    navActionManager = navActionManager,
                )
            }
        }
    }//: Column
}

@Preview
@Composable
fun UserStatsViewPreview() {
    AniHyouTheme {
        Surface {
            UserStatsContent(
                uiState = UserStatsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager(),
                nestedScrollConnection = rememberNestedScrollInteropConnection()
            )
        }
    }
}