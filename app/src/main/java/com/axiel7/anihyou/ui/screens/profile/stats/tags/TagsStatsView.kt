package com.axiel7.anihyou.ui.screens.profile.stats.tags

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.stats.StatDistributionType
import com.axiel7.anihyou.fragment.TagStat
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.ui.screens.profile.stats.composables.DistributionTypeChips
import com.axiel7.anihyou.ui.screens.profile.stats.composables.PositionalStatItemView
import com.axiel7.anihyou.ui.screens.profile.stats.composables.PositionalStatItemViewPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun TagsStatsView(
    stats: List<TagStat>?,
    isLoading: Boolean,
    mediaType: MediaType,
    setMediaType: (MediaType) -> Unit,
    tagsType: StatDistributionType,
    setTagsType: (StatDistributionType) -> Unit,
    navigateToExplore: (tag: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = bottomBarPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                MediaType.knownEntries.forEach {
                    FilterSelectionChip(
                        selected = mediaType == it,
                        text = it.localized(),
                        onClick = { setMediaType(it) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            InfoTitle(
                text = stringResource(R.string.tags)
            )
            DistributionTypeChips(
                value = tagsType,
                onValueChanged = setTagsType,
            )
        }
        if (isLoading) {
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
            key = { index, stat -> stat.tag?.id ?: index },
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
                    stat.tag?.name?.let(navigateToExplore)
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
                stats = null,
                isLoading = true,
                mediaType = MediaType.ANIME,
                setMediaType = {},
                tagsType = StatDistributionType.TITLES,
                setTagsType = {},
                navigateToExplore = {},
            )
        }
    }
}