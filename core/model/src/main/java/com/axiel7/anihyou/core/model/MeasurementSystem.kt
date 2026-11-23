package com.axiel7.anihyou.core.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R
import kotlinx.collections.immutable.toImmutableMap

enum class MeasurementSystem(
    val rawValue: String,
) : Localizable {
    IMPERIAL("IMPERIAL"),
    METRIC("METRIC");

    val stringRes
        get() = when (this) {
            IMPERIAL -> R.string.imperial
            METRIC -> R.string.metric
        }

    @Composable
    override fun localized() = stringResource(stringRes)

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }.toImmutableMap()

        fun safeValueOf(rawValue: String): MeasurementSystem = when(rawValue) {
            "IMPERIAL" -> IMPERIAL
            "METRIC" -> METRIC
            else -> METRIC // default
        }
    }
}