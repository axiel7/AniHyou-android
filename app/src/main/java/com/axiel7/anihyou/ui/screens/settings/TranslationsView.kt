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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.PlainPreference
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import java.util.Locale

private val translations = mapOf(
    "ar-SA" to "Hussain889, Hussain69o, WhiteCanvas, sakugaky, Comikazie, mlvin, bobteen1",
    "in-ID" to "Clxf12",
    "it-IT" to "maicol07, DomeF",
    "ja-JP" to "axiel7",
    "pt-BR" to "Crono0, Torti, RickyM7, SamOak",
    "ru-RU" to "Ronner231, grin3671",
    "es-ES" to "axiel7",
    "tr-TR" to "hsinankirdar",
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
                val displayName = remember { Locale.forLanguageTag(lang).displayName }
                PlainPreference(
                    title = displayName,
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