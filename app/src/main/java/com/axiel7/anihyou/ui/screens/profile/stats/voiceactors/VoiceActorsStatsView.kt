package com.axiel7.anihyou.ui.screens.profile.stats.voiceactors

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
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.stats.StatDistributionType
import com.axiel7.anihyou.fragment.VoiceActorStat
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.screens.profile.stats.composables.DistributionTypeChips
import com.axiel7.anihyou.ui.screens.profile.stats.composables.PositionalStatItemView
import com.axiel7.anihyou.ui.screens.profile.stats.composables.PositionalStatItemViewPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun VoiceActorsStatsView(
    stats: List<VoiceActorStat>?,
    isLoading: Boolean,
    voiceActorsType: StatDistributionType,
    setVoiceActorsType: (StatDistributionType) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = bottomBarPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            InfoTitle(
                text = stringResource(R.string.voice_actors)
            )
            DistributionTypeChips(
                value = voiceActorsType,
                onValueChanged = setVoiceActorsType,
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
            key = { index, stat -> stat.voiceActor?.id ?: index },
            contentType = { _, stat -> stat }
        ) { index, stat ->
            PositionalStatItemView(
                name = stat.voiceActor?.name?.userPreferred ?: stringResource(R.string.unknown),
                position = index + 1,
                count = stat.count,
                meanScore = stat.meanScore,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                minutesWatched = stat.minutesWatched,
                chaptersRead = stat.chaptersRead,
                imageUrl = stat.voiceActor?.image?.medium,
                onClick = {
                    stat.voiceActor?.id?.let(navigateToStaffDetails)
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
            VoiceActorsStatsView(
                stats = null,
                isLoading = true,
                voiceActorsType = StatDistributionType.TITLES,
                setVoiceActorsType = {},
                navigateToStaffDetails = {},
            )
        }
    }
}