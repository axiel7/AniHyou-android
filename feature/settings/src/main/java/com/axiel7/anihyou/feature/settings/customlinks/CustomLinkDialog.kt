package com.axiel7.anihyou.feature.settings.customlinks

import android.webkit.URLUtil
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.axiel7.anihyou.core.base.CUSTOM_URL_NAME_PLACEHOLDER
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

private enum class SpaceSeparator(val value: Char) {
    Percent('%'),
    Plus('+'),
    Minus('-'),
    Underscore('_'),
    Space(' ');

    companion object {
        fun findValue(string: String) = entries.find { string.contains(it.value) }
    }
}

@Composable
fun CustomLinkDialog(
    mediaType: MediaType,
    value: String?,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSeparator by remember(value) {
        mutableStateOf(
            value?.let(SpaceSeparator::findValue) ?: SpaceSeparator.Percent
        )
    }
    var urlValue by remember { mutableStateOf(value?.substring(1).orEmpty()) }
    var urlHasPlaceholder by remember { mutableStateOf(true) }
    var isUrlValid by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text(text = mediaType.localized()) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = urlValue,
                    onValueChange = {
                        urlValue = it
                        urlHasPlaceholder = it.contains(CUSTOM_URL_NAME_PLACEHOLDER)
                    },
                    modifier = Modifier.focusRequester(focusRequester),
                    label = { Text(text = "URL") },
                    placeholder = {
                        Text(text = "https://example.com/?q=$CUSTOM_URL_NAME_PLACEHOLDER")
                    },
                    supportingText = {
                        if (!urlHasPlaceholder) {
                            Text(text = stringResource(R.string.custom_link_url_error_name))
                        } else if (!isUrlValid) {
                            Text(text = stringResource(R.string.invalid_url_error))
                        }
                    },
                    isError = !urlHasPlaceholder || !isUrlValid,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Uri,
                    )
                )

                Text(text = stringResource(R.string.space_separator))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpaceSeparator.entries.fastForEach { separator ->
                        FilterChip(
                            selected = selectedSeparator == separator,
                            onClick = { selectedSeparator = separator },
                            label = { Text(text = separator.value.toString()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    isUrlValid = URLUtil.isValidUrl(urlValue)
                    if (isUrlValid) {
                        onConfirm(selectedSeparator.value + urlValue)
                    }
                },
                enabled = urlHasPlaceholder && urlValue.isNotBlank()
            ) {
                Text(text = stringResource(R.string.ok))
            }
        }
    )
}

@Preview
@Composable
private fun CustomLinkDialogPreview() {
    AniHyouTheme {
        Scaffold { paddingValues ->
            CustomLinkDialog(
                mediaType = MediaType.ANIME,
                value = null,
                onConfirm = {},
                onDismiss = {},
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}