package com.axiel7.anihyou.data.model.stats.overview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.Localizable

data class LengthDistribution(
    val length: String?
) : Localizable, Colorable {
    @Composable
    override fun primaryColor() = MaterialTheme.colorScheme.primary

    @Composable
    override fun onPrimaryColor() = MaterialTheme.colorScheme.onPrimary

    @Composable
    override fun localized() = length ?: stringResource(R.string.unknown)

    companion object {
        val lengthComparator: (length: String?) -> Int = {
            if (it?.contains('+') == true) { //ex: 101+
                it.length * 2
            } else { //ex: 29-55 or null
                it?.length ?: Int.MAX_VALUE
            }
        }
    }
}