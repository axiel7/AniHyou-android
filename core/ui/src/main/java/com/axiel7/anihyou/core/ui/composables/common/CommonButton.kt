package com.axiel7.anihyou.core.ui.composables.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ErrorTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shapes = ButtonDefaults.shapes(),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text(text = text)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MoreLessButton(
    isExpanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shapes = ButtonDefaults.shapes(),
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