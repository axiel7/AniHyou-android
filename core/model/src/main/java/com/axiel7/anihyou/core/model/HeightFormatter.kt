package com.axiel7.anihyou.core.model

import com.axiel7.anihyou.core.network.fragment.Height
import kotlin.math.roundToInt

object HeightFormatter {

    fun format(height: Height?): String? {
        if (height == null) return null

        // when initial is set, then min and max are null
        height.initial?.let {
            return formatRange(metricMin = it.metric, imperialMin = it.imperial)
        }

        val min = height.min
        val max = height.max

        return when {
            min != null && max != null ->
                formatRange(
                    metricMin = min.metric,
                    imperialMin = min.imperial,
                    metricMax = max.metric,
                    imperialMax = max.imperial,
                )

            min != null -> formatRange(metricMin = min.metric, imperialMin = min.imperial)
            max != null -> formatRange(metricMin = max.metric, imperialMin = max.imperial)
            else -> null
        }
    }

    private fun formatRange(
        metricMin: Double?,
        imperialMin: String?,
        metricMax: Double? = null,
        imperialMax: String? = null,
    ): String? {
        val metricText = metricRange(metricMin, metricMax)
        val imperialText = imperialRange(imperialMin, imperialMax)

        return when {
            metricText != null && imperialText != null -> "$metricText ($imperialText)"
            else -> metricText ?: imperialText
        }
    }

    private fun metricRange(min: Double?, max: Double?): String? {
        val minValue = min?.roundToInt()
        val maxValue = max?.roundToInt()
        return when {
            minValue != null && maxValue != null && minValue != maxValue -> "$minValue - $maxValue cm"
            minValue != null -> "$minValue cm"
            else -> null
        }
    }

    private fun imperialRange(min: String?, max: String?): String? = when {
        min != null && max != null && min != max -> "$min - $max"
        min != null -> min
        else -> null
    }
}
