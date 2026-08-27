package com.axiel7.anihyou.core.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.network.type.RecommendationSort
import com.axiel7.anihyou.core.resources.R

@Composable
fun RecommendationSort.localized() = when (this) {
    RecommendationSort.ID -> stringResource(R.string.id)
    RecommendationSort.ID_DESC -> stringResource(R.string.id)
    RecommendationSort.RATING -> stringResource(R.string.sort_score)
    RecommendationSort.RATING_DESC -> stringResource(R.string.sort_score)
    RecommendationSort.UNKNOWN__ -> stringResource(R.string.unknown)
}

enum class RecommendationSortSearch(
    val asc: RecommendationSort,
    val desc: RecommendationSort
) : Localizable {
    ID(
        asc = RecommendationSort.ID,
        desc = RecommendationSort.ID_DESC,
    ),
    RATING(
        asc = RecommendationSort.RATING,
        desc = RecommendationSort.RATING_DESC,
    );

    @Composable
    override fun localized() = when (this) {
        ID -> stringResource(R.string.recent)
        RATING -> stringResource(R.string.sort_score)
    }

    companion object {
        fun valueOf(value: RecommendationSort) = entries.find {
            it.desc == value || it.asc == value
        }
    }
}
