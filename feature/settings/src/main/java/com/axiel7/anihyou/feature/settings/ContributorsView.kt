package com.axiel7.anihyou.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.common.utils.ContextUtils.openActionView
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithLargeTopAppBar
import com.axiel7.anihyou.core.ui.composables.PlainPreference
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.preferenceShape
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Stable
data class Contributor(
    val name: String,
    val url: String,
)

private val contributors = listOf(
    Contributor("axiel7", "https://github.com/axiel7"),
    Contributor("x8laye4r", "https://github.com/x8laye4r"),
    Contributor("uragiristereo", "https://github.com/uragiristereo"),
    Contributor("MagnarIUK", "https://github.com/MagnarIUK"),
    Contributor("fewwan", "https://github.com/fewwan"),
    Contributor("Kramoule", "https://github.com/Kramoule"),
    Contributor("CrazyDiamond4444", "https://github.com/CrazyDiamond4444"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributorsView() {
    val navActionManager = LocalNavActionManager.current
    val context = LocalContext.current
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState()
    )
    DefaultScaffoldWithLargeTopAppBar(
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
            contributors.forEachIndexed { index, item ->
                PlainPreference(
                    title = item.name,
                    onClick = { context.openActionView(item.url) },
                    iconPadding = PaddingValues(vertical = 16.dp),
                    showIconSpacer = false,
                    shape = preferenceShape(index, contributors.size),
                )
            }
        }
    }
}

@Preview
@Composable
private fun ContributorsViewPreview() {
    AniHyouTheme {
        ContributorsView()
    }
}