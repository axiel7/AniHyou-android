package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.ModalBottomSheet
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListStatusSheet(
    selectedStatus: MediaListStatus?,
    mediaType: MediaType,
    scope: CoroutineScope,
    bottomPadding: Dp,
    onStatusChanged: (MediaListStatus?) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissed = onDismiss,
        scope = scope,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp + bottomPadding)
        ) {
            StatusItem(
                status = null,
                mediaType = mediaType,
                isSelected = selectedStatus == null,
                onClick = {
                    onStatusChanged(null)
                    onDismiss()
                }
            )
            MediaListStatus.knownEntries.forEach {
                StatusItem(
                    status = it,
                    mediaType = mediaType,
                    isSelected = selectedStatus == it,
                    onClick = {
                        onStatusChanged(it)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusItem(
    status: MediaListStatus?,
    mediaType: MediaType,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(status.icon()),
            contentDescription = "check",
            tint = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = status.localized(mediaType = mediaType),
            modifier = Modifier.padding(start = 8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}