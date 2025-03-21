package com.axiel7.anihyou.feature.profile.stats.tags

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.InfoTitle
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.profile.stats.UserStatsEvent
import com.axiel7.anihyou.feature.profile.stats.UserStatsUiState
import com.axiel7.anihyou.feature.profile.stats.composables.DistributionTypeChips
import com.axiel7.anihyou.feature.profile.stats.composables.MediaTypeChips
import com.axiel7.anihyou.feature.profile.stats.composables.PositionalStatItemView
import com.axiel7.anihyou.feature.profile.stats.composables.PositionalStatItemViewPlaceholder

@Composable
fun TagsStatsView(
    uiState: UserStatsUiState,
    event: UserStatsEvent?,
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier,
) {
    val stats = if (uiState.mediaType == MediaType.ANIME) uiState.animeTags
    else uiState.mangaTags

    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = bottomBarPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            MediaTypeChips(
                value = uiState.mediaType,
                onValueChanged = { event?.setMediaType(it) },
            )

            InfoTitle(
                text = stringResource(R.string.tags)
            )
            DistributionTypeChips(
                value = uiState.tagsType,
                onValueChanged = { event?.setTagsType(it) },
            )
        }
        if (uiState.isLoading) {
            items(
                count = 3,
                contentType = { it }
            ) {
                PositionalStatItemViewPlaceholder(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        itemsIndexed(
            items = stats.orEmpty(),
            contentType = { _, stat -> stat }
        ) { index, stat ->
            PositionalStatItemView(
                name = stat.tag?.name ?: stringResource(R.string.unknown),
                position = index + 1,
                count = stat.count,
                meanScore = stat.meanScore,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                minutesWatched = stat.minutesWatched,
                chaptersRead = stat.chaptersRead,
                onClick = {
                    navActionManager.toGenreTag(uiState.mediaType, null, stat.tag?.name)
                }
            )
        }
    }//:LazyColumn
}

@Preview
@Composable
fun GenresTagsStatsViewPreview() {
    AniHyouTheme {
        Surface {
            TagsStatsView(
                uiState = UserStatsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}