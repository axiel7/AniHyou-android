package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.model.media.isBehind
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.NumberUtils.format
import com.axiel7.anihyou.utils.StringUtils.orUnknown

@Composable
fun AiringScheduleText(
    item: UserMediaListQuery.MediaList,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
) {
    item.media?.nextAiringEpisode?.let { nextAiringEpisode ->
        val isBehind =
            item.basicMediaListEntry.isBehind(nextAiringEpisode = nextAiringEpisode.episode)
        Text(
            text =
            if (isBehind)
                stringResource(
                    R.string.num_episodes_behind,
                    ((nextAiringEpisode.episode - 1) - (item.basicMediaListEntry.progress ?: 0))
                        .format()
                        .orUnknown()
                )
            else
                stringResource(
                    R.string.episode_in_time,
                    nextAiringEpisode.episode,
                    nextAiringEpisode.timeUntilAiring.toLong().secondsToLegibleText()
                ),
            modifier = modifier,
            color = if (isBehind) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = fontSize,
            textAlign = textAlign,
            lineHeight = fontSize
        )
    }
}