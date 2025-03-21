package com.axiel7.anihyou.core.model.stats

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.network.type.UserStatisticsSort
import com.axiel7.anihyou.core.resources.R

enum class StatDistributionType : com.axiel7.anihyou.core.model.base.Localizable {
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