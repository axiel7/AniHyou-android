package com.axiel7.anihyou.ui.composables.person

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun PersonItemSmall(
    avatarUrl: String?,
    username: String?,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PersonImage(
            url = avatarUrl,
            modifier = Modifier.size(PERSON_IMAGE_SIZE_VERY_SMALL.dp)
        )
        Text(
            text = username ?: "",
            modifier = Modifier.padding(start = 8.dp),
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    }
}