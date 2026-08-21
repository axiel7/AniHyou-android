package com.axiel7.anihyou.core.ui.composables.media

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.resources.stat_dark_10
import com.axiel7.anihyou.core.resources.stat_dark_30
import com.axiel7.anihyou.core.resources.stat_dark_50
import com.axiel7.anihyou.core.resources.stat_dark_on10
import com.axiel7.anihyou.core.resources.stat_dark_on30
import com.axiel7.anihyou.core.resources.stat_dark_on50
import com.axiel7.anihyou.core.resources.stat_light_10
import com.axiel7.anihyou.core.resources.stat_light_30
import com.axiel7.anihyou.core.resources.stat_light_50
import com.axiel7.anihyou.core.resources.stat_light_on10
import com.axiel7.anihyou.core.resources.stat_light_on30
import com.axiel7.anihyou.core.resources.stat_light_on50
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme

@Stable
data class PriorityColors(
    val background: Color,
    val iconTint: Color,
) {
    companion object {

        fun ColorScheme.toPriorityColors() = PriorityColors(
            background = primaryContainer,
            iconTint = onPrimaryContainer,
        )

        fun Color.toPriorityColors(isDark: Boolean) = dynamicColorScheme(
            seedColor = this,
            isDark = isDark,
            style = PaletteStyle.Fidelity,
        ).toPriorityColors()

        @get:Composable
        val DefaultLow
            get() = PriorityColors(
                background = 0.priorityDefaultBackgroundColor(),
                iconTint = 0.priorityDefaultTintColor(),
            )

        @get:Composable
        val DefaultMedium
            get() = PriorityColors(
                background = 1.priorityDefaultBackgroundColor(),
                iconTint = 1.priorityDefaultTintColor(),
            )

        @get:Composable
        val DefaultHigh
            get() = PriorityColors(
                background = 2.priorityDefaultBackgroundColor(),
                iconTint = 2.priorityDefaultTintColor(),
            )

        @get:Composable
        val DefaultNone
            get() = PriorityColors(
                background = MaterialTheme.colorScheme.secondaryContainer,
                iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
    }
}

@Stable
data class AllPriorityColors(
    val low: PriorityColors,
    val medium: PriorityColors,
    val high: PriorityColors,
) {

    @Composable
    fun forPriority(priority: Int) = when (priority) {
        0 -> low
        1 -> medium
        2 -> high
        else -> PriorityColors.DefaultNone
    }

    companion object {
        @get:Composable
        val Default
            get() = AllPriorityColors(
                low = PriorityColors.DefaultLow,
                medium = PriorityColors.DefaultMedium,
                high = PriorityColors.DefaultHigh,
            )
    }
}

fun Int.priorityIcon(): Int = when (this) {
    0 -> R.drawable.counter_0_24
    1 -> R.drawable.counter_1_24
    2 -> R.drawable.counter_2_24
    else -> R.drawable.cancel_24 // invalid priority was set
}

@Composable
fun Int.priorityDefaultBackgroundColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        0 -> if (isDark) stat_dark_10 else stat_light_10
        1 -> if (isDark) stat_dark_30 else stat_light_30
        2 -> if (isDark) stat_dark_50 else stat_light_50
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
}

@Composable
fun Int.priorityDefaultTintColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        0 -> if (isDark) stat_dark_on10 else stat_light_on10
        1 -> if (isDark) stat_dark_on30 else stat_light_on30
        2 -> if (isDark) stat_dark_on50 else stat_light_on50
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }
}

@Composable
fun PriorityIndicator(
    modifier: Modifier = Modifier,
    priority: Int,
    allPriorityColors: AllPriorityColors,
    shape: RoundedCornerShape = RoundedCornerShape(topEnd = 8.dp, bottomStart = 16.dp)
) {
    val priorityColors = allPriorityColors.forPriority(priority)

    Row(
        modifier = modifier
            .clip(shape)
            .background(priorityColors.background)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(priority.priorityIcon()),
            contentDescription = stringResource(R.string.priority),
            modifier = Modifier.size(20.dp),
            tint = priorityColors.iconTint,
        )
    }
}