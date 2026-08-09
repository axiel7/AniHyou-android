package com.axiel7.anihyou.feature.settings.priority_colors

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.core.base.event.UiEvent

@Stable
interface PriorityColorEvent : UiEvent {
    fun onHighPriorityColorChanged(color: Color)
    fun onMediumPriorityColorChanged(color: Color)
    fun onLowPriorityColorChanged(color: Color)
}
