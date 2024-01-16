package com.axiel7.anihyou.ui.composables.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaListStatus

@Composable
fun BoxScope.ListStatusBadgeIndicator(
    alignment: Alignment,
    status: MediaListStatus,
) {
    val shape = when (alignment) {
        Alignment.TopStart -> RoundedCornerShape(topStart = 8.dp, bottomEnd = 16.dp)
        Alignment.BottomEnd -> RoundedCornerShape(topStart = 16.dp, bottomEnd = 8.dp)
        Alignment.BottomStart -> RoundedCornerShape(bottomStart = 8.dp, topEnd = 16.dp)
        Alignment.TopEnd -> RoundedCornerShape(topEnd = 8.dp, bottomStart = 16.dp)
        else -> RoundedCornerShape(16.dp)
    }
    Row(
        modifier = Modifier
            .align(alignment)
            .clip(shape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(status.icon()),
            contentDescription = status.localized()
        )
    }
}