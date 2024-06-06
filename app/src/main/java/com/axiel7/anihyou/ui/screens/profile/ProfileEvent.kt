package com.axiel7.anihyou.ui.screens.profile

import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.ui.common.event.PagedEvent

interface ProfileEvent : PagedEvent {
    fun toggleFollow()
    fun toggleLikeActivity(id: Int)
    fun setColor(color: Color)
}