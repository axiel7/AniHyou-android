package com.axiel7.anihyou.data.model.stats

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.Localizable

data class YearDistribution(
    val year: Int
) : Localizable, Colorable {
    @Composable
    override fun primaryColor() = MaterialTheme.colorScheme.primary

    @Composable
    override fun onPrimaryColor() = MaterialTheme.colorScheme.onPrimary

    @Composable
    override fun localized() = year.toString()

    enum class Type : Localizable {
        TITLES, TIME, SCORE;

        @Composable
        override fun localized() = when (this) {
            TITLES -> stringResource(R.string.title_count)
            TIME -> stringResource(R.string.time_spent)
            SCORE -> stringResource(R.string.mean_score)
        }
    }
}