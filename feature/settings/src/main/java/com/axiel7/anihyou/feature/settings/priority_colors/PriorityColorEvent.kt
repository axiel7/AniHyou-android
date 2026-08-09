package com.axiel7.anihyou.feature.settings.priority_colors

import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.core.base.event.UiEvent

interface PriorityColorEvent : UiEvent {
    fun onHighPriorityColorChanged(color: Color)
    fun onMediumPriorityColorChanged(color: Color)
    fun onLowPriorityColorChanged(color: Color)
    fun updateColors(colorLow: Color, colorMedium: Color, colorHigh: Color)
}
