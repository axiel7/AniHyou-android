package com.axiel7.anihyou.ui.composables.person

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun PersonItemSmall(
    avatarUrl: String?,
    username: String?,
    modifier: Modifier = Modifier,
    isPrivate: Boolean? = null,
    isLocked: Boolean? = null,
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
            text = username.orEmpty(),
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = fontSize,
            fontWeight = fontWeight
        )
        if (isPrivate == true) {
            Icon(
                painter = painterResource(R.drawable.visibility_off_24),
                contentDescription = stringResource(R.string.list_private),
                modifier = Modifier.size(20.dp),
            )
        }
        if (isLocked == true) {
            Icon(
                painter = painterResource(R.drawable.lock_24),
                contentDescription = stringResource(R.string.locked),
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Preview
@Composable
fun PersonItemSmallPreview() {
    AniHyouTheme {
        Surface {
            PersonItemSmall(
                avatarUrl = null,
                username = "axiel7",
                isPrivate = true,
                isLocked = true,
            )
        }
    }
}