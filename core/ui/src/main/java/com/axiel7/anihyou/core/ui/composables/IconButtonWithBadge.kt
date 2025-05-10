package com.axiel7.anihyou.core.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun IconButtonWithBadge(
    @DrawableRes icon: Int,
    badge: @Composable (BoxScope.() -> Unit),
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = LocalContentColor.current,
        )
    ) {
        BadgedBox(
            badge = badge,
            modifier = Modifier
                .padding(end = 2.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "badge",
            )
        }
    }
}

@Preview
@Composable
fun IconButtonWithBadgePreview() {
    AniHyouTheme {
        Surface {
            IconButtonWithBadge(
                icon = R.drawable.notifications_24,
                badge = {
                    Badge {
                        Text(text = "99")
                    }
                },
                onClick = {}
            )
        }
    }
}