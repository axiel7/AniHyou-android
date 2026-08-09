package com.axiel7.anihyou.core.ui.composables.scores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.model.media.priorityIcon
import com.axiel7.anihyou.core.resources.R
import com.materialkolor.ktx.harmonize

@Composable
fun PriorityIndicator(
    modifier: Modifier = Modifier,
    priority: Int,
    lowPriorityColor: Color? = null,
    mediumPriorityColor: Color? = null,
    highPriorityColor: Color? = null,
) {
    val shape = RoundedCornerShape(topEnd = 8.dp, bottomStart = 16.dp)
    val iconId = priority.priorityIcon()
    val backgroundColor = when (priority) {
        0 -> lowPriorityColor ?: MaterialTheme.colorScheme.secondaryContainer
        1 -> mediumPriorityColor ?: MaterialTheme.colorScheme.secondaryContainer
        2 -> highPriorityColor ?: MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    val icon = painterResource(iconId)


    Row(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor.harmonize(MaterialTheme.colorScheme.primary))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = icon,
            contentDescription = stringResource(R.string.priority),
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}