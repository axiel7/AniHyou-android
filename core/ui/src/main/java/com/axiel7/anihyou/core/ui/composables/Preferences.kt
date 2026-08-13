package com.axiel7.anihyou.core.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.common.SmallCircularProgressIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import kotlin.math.roundToInt

@Composable
fun PreferencesTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(start = 72.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun PlainPreference(
    title: String,
    modifier: Modifier = Modifier,
    titleTint: Color = MaterialTheme.colorScheme.onSurface,
    subtitle: String? = null,
    @DrawableRes icon: Int? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconPadding: PaddingValues = PaddingValues(16.dp),
    enabled: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = title,
                    modifier = Modifier.padding(iconPadding),
                    tint = if (enabled) iconTint else iconTint.copy(alpha = 0.38f)
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .padding(iconPadding)
                        .size(24.dp)
                )
            }

            Column(
                modifier = if (subtitle != null)
                    Modifier.padding(16.dp)
                else Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    color = if (enabled) titleTint else titleTint.copy(alpha = 0.38f)
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }//: Column
        }//: Row
        if (isLoading) {
            SmallCircularProgressIndicator(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }//: Row
}

@Composable
fun SwitchPreference(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    preferenceValue: Boolean?,
    @DrawableRes icon: Int? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    iconPadding: PaddingValues = PaddingValues(16.dp),
    onValueChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onValueChange(preferenceValue?.not() ?: false)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = title,
                    modifier = Modifier.padding(iconPadding),
                    tint = iconTint
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .padding(iconPadding)
                        .size(24.dp)
                )
            }

            Column(
                modifier = if (subtitle != null)
                    Modifier.padding(16.dp)
                else Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        lineHeight = 14.sp
                    )
                }
            }//: Column
        }//: Row

        Switch(
            checked = preferenceValue ?: false,
            onCheckedChange = {
                onValueChange(it)
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }//: Row
}

@Composable
fun <T> ListPreference(
    title: String,
    values: List<T>,
    modifier: Modifier = Modifier,
    labelForValue: @Composable (T) -> String = { it.toString() },
    preferenceValue: T?,
    @DrawableRes icon: Int? = null,
    onValueChange: (T) -> Unit
) {
    val windowInfo = LocalWindowInfo.current.containerSize
    var openDialog by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { openDialog = true },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(icon),
                contentDescription = title,
                modifier = Modifier.padding(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            Spacer(
                modifier = Modifier
                    .padding(16.dp)
                    .size(24.dp)
            )
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (preferenceValue != null) {
                Text(
                    text = labelForValue(preferenceValue),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
    }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text(text = title) },
            text = {
                LazyColumn(
                    modifier = Modifier.sizeIn(
                        maxHeight = (windowInfo.height - 48).dp
                    )
                ) {
                    items(values) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onValueChange(entry) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = preferenceValue == entry,
                                onClick = { onValueChange(entry) }
                            )
                            Text(text = labelForValue(entry))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { openDialog = false }) {
                    Text(text = stringResource(R.string.close))
                }
            }
        )
    }
}

@Composable
fun <T> ListPreference(
    title: String,
    entriesValues: Map<T, Int>,
    modifier: Modifier = Modifier,
    preferenceValue: T?,
    @DrawableRes icon: Int? = null,
    onValueChange: (T) -> Unit
) {
    ListPreference(
        title = title,
        values = entriesValues.entries.map { it.key },
        labelForValue = { value ->
            entriesValues[value]?.let { stringResource(it) }.orEmpty()
        },
        modifier = modifier,
        preferenceValue = preferenceValue,
        icon = icon,
        onValueChange = onValueChange,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreStepsPreferenceSheet(
    title: String,
    subtitle: String?,
    @DrawableRes icon: Int? = null,
    initialValue: Double = 1.0,
    scoreFormat: ScoreFormat?,
    changeValue: (Double) -> Unit
) {
    val minValue = when (scoreFormat) {
        ScoreFormat.POINT_100 -> 1.0
        ScoreFormat.POINT_10 -> 1.0
        ScoreFormat.POINT_10_DECIMAL -> 0.1
        else -> 1.0
    }

    val maxValue = when (scoreFormat) {
        ScoreFormat.POINT_100 -> 100.0
        ScoreFormat.POINT_10 -> 10.0
        ScoreFormat.POINT_10_DECIMAL -> 10.0
        else -> 10.0
    }

    val allowDecimal = scoreFormat == ScoreFormat.POINT_10_DECIMAL

    var openModal by remember { mutableStateOf(false) }

    var value by remember(initialValue, minValue, maxValue) {
        mutableDoubleStateOf(initialValue.coerceIn(minValue..maxValue))
    }

    var textFieldValue by remember(initialValue) {
        mutableStateOf(if (allowDecimal) initialValue.toString() else initialValue.roundToInt().toString()
        )
    }

    val integerRegex = remember { Regex("^\\d*$") }
    val decimalRegex = remember { Regex("^\\d*\\.?\\d?$") }

    PlainPreference(
        title = title,
        subtitle = subtitle,
        icon = icon,
        onClick = {
            openModal = true
        }
    )

    if (openModal) {
        ModalBottomSheet(
            onDismissRequest = {
                openModal = false
                changeValue(value)
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = title
                )
                Text(
                    text = "$minValue - $maxValue",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { input ->
                        // maybe you have here a better idea here than me
                        val isValid = if (allowDecimal) {
                            input.isEmpty() || input.matches(decimalRegex)
                        } else {
                            input.isEmpty() || input.matches(integerRegex)
                        }

                        if (isValid) {
                            val asNumber = input.toDoubleOrNull()

                            textFieldValue = if (asNumber == null || asNumber <= maxValue) input else textFieldValue
                            if (asNumber != null) {
                                val clamped = asNumber.coerceIn(minValue, maxValue)
                                value = clamped
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (allowDecimal) KeyboardType.Decimal else KeyboardType.Number
                    )
                )

                Slider(
                    value = value.toFloat(),
                    valueRange = minValue.toFloat()..maxValue.toFloat(),
                    onValueChange = { input ->
                        if (allowDecimal) {
                            val rounded = (input * 10f).roundToInt() / 10.0
                            value = rounded.coerceIn(minValue, maxValue)
                            textFieldValue = rounded.toString()
                        } else {
                            val roundedInt = input.roundToInt()
                            value = roundedInt.toDouble().coerceIn(minValue, maxValue)
                            textFieldValue = roundedInt.toString()
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreferencesPreviews() {
    var enabled by remember { mutableStateOf(true) }
    AniHyouTheme {
        Column {
            PreferencesTitle(text = "Preferences")

            PlainPreference(
                title = "Plain Preference",
                subtitle = "Subtitle",
                icon = R.drawable.settings_24,
                onClick = {},
            )

            SwitchPreference(
                title = "Switch Preference",
                subtitle = "Subtitle",
                preferenceValue = enabled,
                icon = R.drawable.settings_24,
                onValueChange = { enabled = it },
            )

            ListPreference(
                title = "List Preference",
                entriesValues = mapOf("Profile" to R.string.profile),
                preferenceValue = null,
                icon = R.drawable.settings_24,
                onValueChange = {},
            )

            ScoreStepsPreferenceSheet(
                title = "BottomSheet Preference",
                subtitle = "Subtitle",
                icon = R.drawable.settings_24,
                scoreFormat = ScoreFormat.POINT_10_DECIMAL,
                initialValue = 2.0,
                changeValue = {}
            )
        }
    }
}