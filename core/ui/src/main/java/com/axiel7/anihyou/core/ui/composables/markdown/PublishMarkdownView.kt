package com.axiel7.anihyou.core.ui.composables.markdown

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishMarkdownView(
    onPublish: (String) -> Unit,
    isLoading: Boolean,
    initialText: String? = null,
    navigateBack: () -> Unit,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )
    var text by remember { mutableStateOf("") }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    painter = painterResource(R.drawable.close_24),
                    contentDescription = stringResource(R.string.close)
                )
            }
        },
        actions = {
            Button(
                onClick = { onPublish(text) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(text = stringResource(R.string.publish))
                }
            }
        },
        scrollBehavior = topAppBarScrollBehavior,
        contentWindowInsets = WindowInsets.statusBars
    ) { padding ->
        MarkdownEditor(
            onValueChanged = { text = it },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding(),
            initialText = initialText
        )
    }
}

@Preview
@Composable
fun PublishMarkdownViewPreview() {
    AniHyouTheme {
        Surface {
            PublishMarkdownView(
                onPublish = {},
                isLoading = true,
                navigateBack = {}
            )
        }
    }
}