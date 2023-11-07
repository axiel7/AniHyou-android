package com.axiel7.anihyou.data.model.stats

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.type.UserStatisticsSort

enum class StatDistributionType : Localizable {
    TITLES, TIME, SCORE;

    @Composable
    override fun localized() = when (this) {
        TITLES -> stringResource(R.string.title_count)
        TIME -> stringResource(R.string.time_spent)
        SCORE -> stringResource(R.string.mean_score)
    }

    fun userStatisticsSort(ascending: Boolean) = when (this) {
        TITLES -> if (ascending) UserStatisticsSort.COUNT else UserStatisticsSort.COUNT_DESC
        TIME -> if (ascending) UserStatisticsSort.PROGRESS else UserStatisticsSort.PROGRESS_DESC
        SCORE -> if (ascending) UserStatisticsSort.MEAN_SCORE else UserStatisticsSort.MEAN_SCORE_DESC
    }
}