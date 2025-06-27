package com.axiel7.anihyou.feature.settings

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.core.base.CROWDIN_URL
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.PlainPreference
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.common.utils.ContextUtils.openActionView
import java.util.Locale

private val translations = mapOf(
    "ar-SA" to "Hussain889, Hussain69o, WhiteCanvas, eyadalomar, sakugaky, Comikazie, mlvin, bobteen1",
    "az-AZ" to "oolyvi",
    "zh-Hans" to "hualiong, MareDevi, Andypsl8, bengerlorf",
    "zh-Hant" to "hualiong, jhih_yu_lin, web790709",
    "fr-FR" to "0xybo, mamanamgae, frosqh, Eria78, nesquick",
    "de-DE" to "LittleFreak, Secresa, MaximilianGT500, notsakin",
    "in-ID" to "Clxf12",
    "it-IT" to "maicol07, DomeF",
    "ja-JP" to "axiel7",
    "pl-PL" to "xiggeush, YOGI_AOGI",
    "pt-BR" to "Crono0, Torti, Ratolino, RickyM7, SamOak",
    "ro-RO" to "alexcsy",
    "ru-RU" to "Ronner231, grin3671, Speech100",
    "es-ES" to "axiel7",
    "th-TH" to "PiyaRawing",
    "tr-TR" to "hsinankirdar",
    "uk-UA" to "Syly_vibes, Sensetivity, magnariuk, DanielleTlumach",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationsView(
    navActionManager: NavActionManager,
) {
    val context = LocalContext.current
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.translations),
        navigationIcon = {
            BackIconButton(onClick = navActionManager::goBack)
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            PlainPreference(
                title = "Crowdin",
                icon = R.drawable.language_24,
                onClick = {
                    context.openActionView(CROWDIN_URL)
                }
            )
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
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}