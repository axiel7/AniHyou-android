package com.axiel7.anihyou.feature.editmedia.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun EditMediaProgressRow(
    label: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    progress: Int?,
    totalProgress: Int?,
    onValueChange: (String) -> Unit,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = label,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            BasicTextField(
                value = progress?.format().orEmpty(),
                onValueChange = onValueChange,
                modifier = Modifier.width(IntrinsicSize.Min),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textIndent = TextIndent(firstLine = 2.sp)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (progress == null) {
                            Text(
                                text = "0",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        innerTextField()
                    }
                }
            )

            if (totalProgress != null) {
                Text(
                    text = "/${totalProgress.format()}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }

            Text(
                text = label,
                modifier = Modifier.padding(
                    start = if (totalProgress == null) 8.dp else 4.dp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }

        FilledTonalIconButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onMinusClick()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.remove_24),
                contentDescription = stringResource(R.string.minus_one)
            )
        }
        FilledTonalIconButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onPlusClick()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.add_24),
                contentDescription = stringResource(R.string.plus_one)
            )
        }
    }
}

@Preview
@Composable
private fun EditMediaProgressRowPreview() {
    AniHyouTheme {
        Surface {
            EditMediaProgressRow(
                label = "Episodes",
                icon = R.drawable.book_24,
                progress = 9999,
                totalProgress = 9999,
                onValueChange = {},
                onMinusClick = {},
                onPlusClick = {}
            )
        }
    }
}