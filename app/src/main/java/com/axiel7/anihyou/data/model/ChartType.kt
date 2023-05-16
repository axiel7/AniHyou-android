package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class ChartType : Localizable {
    TOP_ANIME {
        @Composable override fun localized() = stringResource(R.string.top_100)
    },
    POPULAR_ANIME {
        @Composable override fun localized() = stringResource(R.string.top_popular)
    },
    TOP_MANGA {
        @Composable override fun localized() = stringResource(R.string.top_100)
    },
    POPULAR_MANGA {
        @Composable override fun localized() = stringResource(R.string.top_popular)
    }
}