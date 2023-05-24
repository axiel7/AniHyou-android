package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSelectionChip(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = text) },
        modifier = modifier.padding(horizontal = 4.dp),
        leadingIcon = {
            if (selected) {
                Icon(painter = painterResource(R.drawable.check_24), contentDescription = "check")
            }
        }
    )
}