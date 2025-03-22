package com.axiel7.anihyou.core.ui.composables.media

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.axiel7.anihyou.core.model.media.episodesBehind
import com.axiel7.anihyou.core.model.media.isBehind
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.secondsToLegibleText

@Composable
fun AiringScheduleText(
    item: CommonMediaListEntry,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
) {
    item.media?.nextAiringEpisode?.let { nextAiringEpisode ->
        val isBehind = item.isBehind()
        Text(
            text =
            if (isBehind) {
                val episodes = item.episodesBehind()
                pluralStringResource(
                    id = R.plurals.num_episodes_behind,
                    count = episodes,
                    episodes
                )
            } else {
                stringResource(
                    R.string.episode_in_time,
                    nextAiringEpisode.episode,
                    nextAiringEpisode.timeUntilAiring.toLong().secondsToLegibleText()
                )
            },
            modifier = modifier,
            color = if (isBehind) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = fontSize,
            textAlign = textAlign,
            lineHeight = fontSize
        )
    }
}