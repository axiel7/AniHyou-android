package com.axiel7.anihyou.ui.widget.common

import androidx.compose.runtime.Composable
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.fragment.CommonAiringSchedule
import com.axiel7.anihyou.utils.DateUtils
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.NumberUtils.format
import kotlin.math.absoluteValue

@Composable
fun AiringText(
    schedule: CommonAiringSchedule,
    modifier: GlanceModifier = GlanceModifier
) {
    val airingIn =
        schedule.airingAt.toLong() - DateUtils.currentTimeSeconds()
    val airingText = if (airingIn > 0) {
        val timeText = airingIn.secondsToLegibleText(
            buildString = { id, time ->
                LocalContext.current.getString(id, time.format())
            }
        )
        LocalContext.current.getString(
            R.string.episode_in_time,
            schedule.episode,
            timeText
        )
    } else {
        val timeText =
            airingIn.absoluteValue.secondsToLegibleText(
                buildString = { id, time ->
                    LocalContext.current.getString(id, time.format())
                }
            )
        LocalContext.current.getString(
            R.string.episode_aired_ago,
            schedule.episode,
            timeText
        )
    }
    Text(
        text = airingText,
        modifier = modifier,
        style = TextStyle(
            color = GlanceTheme.colors.onPrimaryContainer
        ),
        maxLines = 1
    )
}