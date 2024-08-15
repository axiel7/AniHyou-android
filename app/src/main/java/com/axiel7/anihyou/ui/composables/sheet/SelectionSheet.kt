package com.axiel7.anihyou.ui.composables.sheet

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.utils.NumberUtils.format
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionSheet(
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    bottomPadding: Dp = 0.dp,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissed = onDismiss,
        modifier = modifier,
        scope = scope,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 8.dp + bottomPadding),
            content = content,
        )
    }
}

@Composable
fun SelectionSheetItem(
    name: String,
    @DrawableRes icon: Int? = null,
    iconTint: Color = LocalContentColor.current,
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
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }

        Text(
            text = name,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
fun SelectionSheetItem(
    name: String,
    @DrawableRes icon: Int? = null,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val tint = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant
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
                modifier = Modifier.size(24.dp),
                tint = tint
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }

        Text(
            text = name,
            modifier = Modifier.padding(start = 8.dp),
            color = tint,
            fontWeight = FontWeight.SemiBold.takeIf { isSelected },
        )
        Spacer(modifier = Modifier.weight(1f))
        if (count > 0) {
            Text(
                text = count.format() ?: count.toString(),
                modifier = Modifier.padding(end = 8.dp),
                color = tint,
                fontWeight = FontWeight.SemiBold.takeIf { isSelected },
                textAlign = TextAlign.End,
            )
        }
    }
}