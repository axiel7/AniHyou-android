package com.axiel7.anihyou.feature.settings.customlinks

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndSelectAll
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.core.net.toUri
import com.axiel7.anihyou.core.base.CUSTOM_URL_NAME_PLACEHOLDER
import com.axiel7.anihyou.core.domain.model.CustomLink
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.model.user.preferenceValues
import com.axiel7.anihyou.core.model.user.stringRes
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import kotlinx.coroutines.flow.collectLatest

private enum class SpaceSeparator(val value: Char) {
    Percent('%'),
    Plus('+'),
    Minus('-'),
    Underscore('_'),
    Space(' ');

    companion object {
        fun findValue(char: Char) = entries.find { char == it.value }
    }
}

@Composable
fun CustomLinkDialog(
    mediaType: MediaType,
    value: CustomLink?,
    onConfirm: (CustomLink) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSeparator by remember(value) {
        mutableStateOf(
            value?.spaceSeparator?.let(SpaceSeparator::findValue) ?: SpaceSeparator.Percent
        )
    }
    var selectedTitleLanguage by remember(value) {
        mutableStateOf(value?.titleLanguage)
    }
    val linkNameState = rememberTextFieldState(initialText = value?.name.orEmpty())
    val urlFieldState = rememberTextFieldState(initialText = value?.uri.orEmpty())
    var urlHasPlaceholder by remember { mutableStateOf(true) }
    var isUrlValid by remember { mutableStateOf(true) }

    LaunchedEffect(urlFieldState) {
        snapshotFlow { urlFieldState.text.toString() }.collectLatest { url ->
            urlHasPlaceholder = url.contains(CUSTOM_URL_NAME_PLACEHOLDER)
            if (urlHasPlaceholder && linkNameState.text.isBlank()) {
                linkNameState.setTextAndSelectAll(CustomLink.extractNameFromUrl(url))
            }
        }
    }

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
                    state = linkNameState,
                    label = { Text(text = stringResource(R.string.link_name)) },
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next,
                    )
                )

                OutlinedTextField(
                    state = urlFieldState,
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
                        imeAction = ImeAction.Done,
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

                Text(text = stringResource(R.string.title_language))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UserTitleLanguage.preferenceValues().forEach { lang ->
                        val selected = selectedTitleLanguage == lang
                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedTitleLanguage = if (selected) null else lang
                            },
                            label = { Text(text = stringResource(lang.stringRes())) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val urlValue = urlFieldState.text.toString()
                    val uri = urlValue.toUri()
                    val intent = runCatching {
                        Intent.parseUri(urlValue, 0)
                    }.getOrNull()
                    isUrlValid = (uri.scheme != null && uri.host != null) || intent != null
                    if (isUrlValid) {
                        val link = CustomLink(
                            id = value?.id ?: 0,
                            name = linkNameState.text.toString(),
                            uri = urlValue,
                            spaceSeparator = selectedSeparator.value,
                            mediaType = mediaType,
                            titleLanguage = selectedTitleLanguage,
                        )
                        onConfirm(link)
                    }
                },
                enabled = urlHasPlaceholder && urlFieldState.text.isNotBlank()
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