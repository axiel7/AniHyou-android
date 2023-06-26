package com.axiel7.anihyou.ui.screens.profile.stats

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.ui.composables.FilterSelectionChip
import com.axiel7.anihyou.ui.theme.AniHyouTheme

enum class UserStatType : Localizable {
    OVERVIEW {
        @Composable
        override fun localized() = stringResource(R.string.overview)
    },
    GENRES {
        @Composable
        override fun localized() = stringResource(R.string.genres)
    },
    TAGS {
        @Composable
        override fun localized() = stringResource(R.string.tags)
    },
    STAFF {
        @Composable
        override fun localized() = stringResource(R.string.staff)
    },
    VOICE_ACTORS {
        @Composable
        override fun localized() = stringResource(R.string.voice_actors)
    },
    STUDIOS {
        @Composable
        override fun localized() = stringResource(R.string.studios)
    }
}

@Composable
fun UserStatsView(
    userId: Int,
    modifier: Modifier = Modifier
) {
    val viewModel: UserStatsViewModel = viewModel { UserStatsViewModel(userId) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            UserStatType.values().forEach {
                FilterSelectionChip(
                    selected = viewModel.statType == it,
                    text = it.localized(),
                    onClick = { viewModel.statType = it }
                )
            }
        }//: Row

        when (viewModel.statType) {
            UserStatType.OVERVIEW -> OverviewUserStatsView(viewModel = viewModel)
            UserStatType.GENRES -> ComingSoonText()
            UserStatType.TAGS -> ComingSoonText()
            UserStatType.STAFF -> ComingSoonText()
            UserStatType.VOICE_ACTORS -> ComingSoonText()
            UserStatType.STUDIOS -> ComingSoonText()
        }
    }//: Column
}

@Composable
fun ComingSoonText() {
    Text(
        text = "Coming Soon",
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
fun UserStatsViewPreview() {
    AniHyouTheme {
        Surface {
            UserStatsView(
                userId = 1
            )
        }
    }
}