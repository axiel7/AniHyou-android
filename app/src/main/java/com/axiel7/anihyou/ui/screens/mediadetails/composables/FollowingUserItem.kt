package com.axiel7.anihyou.ui.screens.mediadetails.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_VERY_SMALL
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.composables.scores.MinimalScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun FollowingUserItem(
    mediaType: MediaType,
    avatarUrl: String?,
    username: String,
    status: MediaListStatus,
    score: Double?,
    scoreFormat: ScoreFormat?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PersonImage(
            url = avatarUrl,
            modifier = Modifier
                .size(PERSON_IMAGE_SIZE_VERY_SMALL.dp)
        )
        Text(
            text = username,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = status.icon()),
                contentDescription = status.localized(mediaType),
            )
            if (score != null && scoreFormat != null) {
                MinimalScoreIndicator(
                    score = score,
                    scoreFormat = scoreFormat,
                    modifier = Modifier
                        .width(64.dp)
                        .padding(start = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(64.dp))
            }
        }
    }
}

@Preview
@Composable
private fun FollowingUserItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                FollowingUserItem(
                    mediaType = MediaType.ANIME,
                    avatarUrl = null,
                    username = "axiel7",
                    status = MediaListStatus.CURRENT,
                    score = 8.8,
                    scoreFormat = ScoreFormat.POINT_10_DECIMAL,
                )
                FollowingUserItem(
                    mediaType = MediaType.MANGA,
                    avatarUrl = null,
                    username = "axiel7",
                    status = MediaListStatus.CURRENT,
                    score = 3.0,
                    scoreFormat = ScoreFormat.POINT_3,
                )
            }
        }
    }
}