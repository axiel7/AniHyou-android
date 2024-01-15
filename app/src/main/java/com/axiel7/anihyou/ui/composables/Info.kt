package com.axiel7.anihyou.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R

@Composable
fun InfoItemView(
    title: String,
    info: String?,
    modifier: Modifier = Modifier,
    lineLimit: Int = 2,
) {
    var maxLines by remember { mutableIntStateOf(lineLimit) }
    var showExpand by remember { mutableStateOf(false) }
    val isExpanded = maxLines != lineLimit
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(modifier)
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(
            modifier = Modifier
                .weight(1.4f)
                .animateContentSize()
        ) {
            SelectionContainer {
                Text(
                    text = info ?: stringResource(R.string.unknown),
                    maxLines = maxLines,
                    onTextLayout = {
                        showExpand = it.hasVisualOverflow
                    }
                )
            }
            if (showExpand || isExpanded) {
                TextButton(
                    onClick = {
                        maxLines = if (isExpanded) lineLimit else Int.MAX_VALUE
                    },
                    contentPadding = PaddingValues(
                        start = 0.dp,
                        top = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp
                    )
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isExpanded) R.drawable.expand_less_24 else R.drawable.expand_more_24
                        ),
                        contentDescription = "expand_arrow",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = stringResource(
                            id = if (isExpanded) R.string.show_less else R.string.show_more
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

@Composable
fun <T> InfoClickableItemView(
    title: String,
    items: List<T>,
    itemName: (T) -> String,
    onItemClicked: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (items.isEmpty()) {
                Text(text = stringResource(R.string.no_information))
            } else {
                items.forEach { item ->
                    AssistChip(
                        onClick = { onItemClicked(item) },
                        label = { Text(text = itemName(item)) }
                    )
                }
            }
        }
    }
}

@Composable
fun InfoTitle(
    text: String,
    trailingIcon: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        trailingIcon()
    }
}
