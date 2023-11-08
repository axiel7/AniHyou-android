package com.axiel7.anihyou.ui.screens.profile.stats.staff

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
import com.axiel7.anihyou.fragment.StaffStat
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.screens.profile.stats.composables.DistributionTypeChips
import com.axiel7.anihyou.ui.screens.profile.stats.composables.MediaTypeChips
import com.axiel7.anihyou.ui.screens.profile.stats.composables.PositionalStatItemView
import com.axiel7.anihyou.ui.screens.profile.stats.composables.PositionalStatItemViewPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun StaffStatsView(
    stats: List<StaffStat>?,
    isLoading: Boolean,
    mediaType: MediaType,
    setMediaType: (MediaType) -> Unit,
    staffType: StatDistributionType,
    setStaffType: (StatDistributionType) -> Unit,
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
            MediaTypeChips(
                value = mediaType,
                onValueChanged = setMediaType
            )

            InfoTitle(
                text = stringResource(R.string.staff)
            )
            DistributionTypeChips(
                value = staffType,
                onValueChanged = setStaffType,
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
            key = { index, stat -> stat.staff?.id ?: index },
            contentType = { _, stat -> stat }
        ) { index, stat ->
            PositionalStatItemView(
                name = stat.staff?.name?.userPreferred ?: stringResource(R.string.unknown),
                position = index + 1,
                count = stat.count,
                meanScore = stat.meanScore,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                minutesWatched = stat.minutesWatched,
                chaptersRead = stat.chaptersRead,
                imageUrl = stat.staff?.image?.medium,
                onClick = {
                    stat.staff?.id?.let(navigateToStaffDetails)
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
            StaffStatsView(
                stats = null,
                isLoading = true,
                mediaType = MediaType.ANIME,
                setMediaType = {},
                staffType = StatDistributionType.TITLES,
                setStaffType = {},
                navigateToStaffDetails = {},
            )
        }
    }
}