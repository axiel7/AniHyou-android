package com.axiel7.anihyou.core.model.stats.overview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.axiel7.anihyou.core.model.base.Colorable
import com.axiel7.anihyou.core.model.base.Localizable

data class YearDistribution(
    val year: Int
) : Localizable, Colorable {
    @Composable
    override fun primaryColor() = MaterialTheme.colorScheme.primary

    @Composable
    override fun onPrimaryColor() = MaterialTheme.colorScheme.onPrimary

    @Composable
    override fun localized() = year.toString()
}