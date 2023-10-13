package com.axiel7.anihyou.ui.screens.settings.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.utils.LocaleUtils
import com.axiel7.anihyou.utils.LocaleUtils.getAvailableLocales

@Composable
fun LanguagePreference(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    var openDialog by remember { mutableStateOf(false) }

    val availableLocales = remember { context.getAvailableLocales() }
    var currentLocale by remember { mutableStateOf(LocaleUtils.getDefaultLocale()) }

    LaunchedEffect(currentLocale) {
        LocaleUtils.setDefaultLocale(currentLocale)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { openDialog = true },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.language_24),
            contentDescription = stringResource(R.string.language),
            modifier = Modifier.padding(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.language),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = availableLocales[currentLocale] ?: stringResource(R.string.theme_system),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text(text = stringResource(R.string.language)) },
            text = {
                LazyColumn(
                    modifier = Modifier.sizeIn(
                        maxHeight = (configuration.screenHeightDp - 48).dp
                    )
                ) {
                    items(availableLocales.toList()) { (tag, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { currentLocale = tag },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLocale == tag,
                                onClick = { currentLocale = tag }
                            )
                            Text(text = name)
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