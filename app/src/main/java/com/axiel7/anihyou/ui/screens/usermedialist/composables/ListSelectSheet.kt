package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.listName
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.ModalBottomSheet
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSelectSheet(
    selectedListName: String?,
    mediaType: MediaType,
    customLists: List<String>,
    scope: CoroutineScope,
    bottomPadding: Dp,
    onListChanged: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissed = onDismiss,
        scope = scope,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 8.dp + bottomPadding)
        ) {
            StatusItem(
                name = stringResource(R.string.all),
                icon = R.drawable.list_alt_24,
                isSelected = selectedListName == null,
                onClick = {
                    onListChanged(null)
                    onDismiss()
                }
            )
            MediaListStatus.knownEntries.forEach { status ->
                val name = status.listName(mediaType)
                StatusItem(
                    name = status.localized(mediaType),
                    icon = status.icon(),
                    isSelected = selectedListName == name,
                    onClick = {
                        onListChanged(name)
                        onDismiss()
                    }
                )
            }
            customLists.forEach { name ->
                StatusItem(
                    name = name,
                    isSelected = selectedListName == name,
                    onClick = {
                        onListChanged(name)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusItem(
    name: String,
    @DrawableRes icon: Int? = null,
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
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = name,
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }

        Text(
            text = name,
            modifier = Modifier.padding(start = 8.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}