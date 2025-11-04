package com.axiel7.anihyou.core.ui.composables.markdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun MarkdownEditor(
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialText: String? = null,
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text = initialText.orEmpty())) }
    var showPasteDialog by remember { mutableStateOf(false) }
    var lastFormat by remember { mutableStateOf<MarkdownFormat?>(null) }

    if (showPasteDialog) {
        PasteLinkDialog(
            onConfirm = { link ->
                val newText = textFieldValue.text + lastFormat?.syntax?.replace("%s", link)
                textFieldValue = textFieldValue.copy(
                    text = newText,
                    selection = TextRange(
                        newText.length - (lastFormat?.selectionOffset ?: 0)
                    )
                )
                onValueChanged(newText)
                showPasteDialog = false
            },
            onDismiss = { showPasteDialog = false }
        )
    }

    LaunchedEffect(showPasteDialog) {
        if (!showPasteDialog) focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .imePadding(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    onValueChanged(it.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(text = stringResource(R.string.write_something))
                },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                singleLine = false,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
        }
        MarkdownFormattingButtons(
            onButtonClicked = { format ->
                lastFormat = format
                when (format) {
                    MarkdownFormat.LINK, MarkdownFormat.IMAGE, MarkdownFormat.YOUTUBE, MarkdownFormat.VIDEO -> {
                        showPasteDialog = true
                    }

                    else -> {
                        val newText = textFieldValue.text + format.syntax
                        textFieldValue = textFieldValue.copy(
                            text = newText,
                            selection = TextRange(newText.length - format.selectionOffset)
                        )
                        onValueChanged(newText)
                    }
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        )
    }//:Column
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MarkdownFormattingButtons(
    onButtonClicked: (MarkdownFormat) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(modifier = modifier) {
        MarkdownFormat.entries.forEach { format ->
            IconButton(
                onClick = { onButtonClicked(format) },
                shapes = IconButtonDefaults.shapes()
            ) {
                Icon(
                    painter = painterResource(format.icon),
                    contentDescription = format.localized()
                )
            }
        }
    }
}

@Composable
fun PasteLinkDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var text by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .focusRequester(focusRequester),
                label = {
                    Text(text = stringResource(R.string.paste_your_link))
                }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MarkdownEditorPreview() {
    AniHyouTheme {
        Surface {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Post something") },
                    )
                }
            ) { padding ->
                MarkdownEditor(
                    onValueChanged = { },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }
}