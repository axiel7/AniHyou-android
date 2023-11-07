package com.axiel7.anihyou.data.model.stats

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class StatDistributionType : Localizable {
    TITLES, TIME, SCORE;

    @Composable
    override fun localized() = when (this) {
        TITLES -> stringResource(R.string.title_count)
        TIME -> stringResource(R.string.time_spent)
        SCORE -> stringResource(R.string.mean_score)
    }
}