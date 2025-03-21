package com.axiel7.anihyou.feature.profile.stats.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.person.PersonImage
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.DateUtils.secondsToLegibleText

@Composable
fun PositionalStatItemView(
    name: String,
    position: Int,
    count: Int,
    meanScore: Double,
    modifier: Modifier = Modifier,
    minutesWatched: Int? = null,
    chaptersRead: Int? = null,
    imageUrl: String? = null,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUrl != null) {
                PersonImage(
                    url = imageUrl,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(50.dp)
                )
            }
            Text(
                text = name,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$position",
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextSubtitleVertical(
                text = count.format(),
                subtitle = stringResource(R.string.title_count)
            )
            TextSubtitleVertical(
                text = meanScore.format(),
                subtitle = stringResource(R.string.mean_score)
            )
            if (minutesWatched != null) {
                TextSubtitleVertical(
                    text = (minutesWatched * 60L).secondsToLegibleText(isFutureDate = false),
                    subtitle = stringResource(R.string.time_spent)
                )
            } else if (chaptersRead != null) {
                TextSubtitleVertical(
                    text = chaptersRead.format(),
                    subtitle = stringResource(R.string.chapters_read)
                )
            }
        }
    }
}

@Composable
fun PositionalStatItemViewPlaceholder(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Loading",
                modifier = Modifier.defaultPlaceholder(visible = true)
            )
            Text(
                text = "#10",
                modifier = Modifier.defaultPlaceholder(visible = true)
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                Spacer(
                    modifier = Modifier
                        .size(48.dp)
                        .defaultPlaceholder(visible = true)
                )
            }
        }
    }
}

@Preview
@Composable
fun PositionalStatItemViewPreview() {
    AniHyouTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PositionalStatItemView(
                    name = "Action",
                    position = 10,
                    count = 399,
                    meanScore = 81.79,
                    minutesWatched = 129378
                )
                PositionalStatItemView(
                    name = "Drama",
                    position = 10,
                    count = 399,
                    meanScore = 81.79,
                    chaptersRead = 2346
                )
                PositionalStatItemView(
                    name = "Kamiya Hiroshi",
                    position = 10,
                    count = 399,
                    meanScore = 81.79,
                    minutesWatched = 12383278,
                    imageUrl = ""
                )
                PositionalStatItemViewPlaceholder()
            }
        }
    }
}