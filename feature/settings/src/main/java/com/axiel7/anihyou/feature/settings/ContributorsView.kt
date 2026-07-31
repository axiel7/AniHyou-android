package com.axiel7.anihyou.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.core.common.utils.ContextUtils.openActionView
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.PlainPreference
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

private val contributors = mapOf(
    "axiel7" to "https://github.com/axiel7",
    "uragiristereo" to "https://github.com/uragiristereo",
    "x8laye4r" to "https://github.com/x8laye4r",
    "fewwan" to "https://github.com/fewwan",
    "Kramoule" to "https://github.com/Kramoule",
    "CrazyDiamond4444" to "https://github.com/CrazyDiamond4444",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributorsView(
    navActionManager: NavActionManager,
) {
    val context = LocalContext.current
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.contributors),
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
            contributors.forEach { (name, link) ->
                PlainPreference(
                    title = name,
                    onClick = { context.openActionView(link) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ContributorsViewPreview() {
    AniHyouTheme {
        ContributorsView(
            navActionManager = NavActionManager.rememberNavActionManager()
        )
    }
}