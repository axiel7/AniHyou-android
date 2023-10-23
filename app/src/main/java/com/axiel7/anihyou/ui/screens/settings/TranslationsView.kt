package com.axiel7.anihyou.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.PlainPreference
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val TRANSLATIONS_DESTINATION = "translations"

private val translations = mapOf(
    R.string.japanese to "axiel7",
    R.string.russian to "Ronner231, grin3671",
    R.string.spanish to "axiel7",
    R.string.turkish to "hsinankirdar",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationsView(
    navigateBack: () -> Unit,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.translations),
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            translations.forEach { (lang, users) ->
                PlainPreference(
                    title = stringResource(lang),
                    subtitle = users,
                    onClick = {}
                )
            }
        }
    }
}

@Preview
@Composable
fun TranslationsViewPreview() {
    AniHyouTheme {
        Surface {
            TranslationsView(
                navigateBack = {}
            )
        }
    }
}