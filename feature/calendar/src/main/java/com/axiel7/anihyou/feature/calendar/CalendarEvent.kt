package com.axiel7.anihyou.feature.calendar

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.ExploreMedia

@Immutable
interface CalendarEvent : UiEvent, PagedEvent {
    fun onUpdateListEntry(viewListEntry: BasicMediaListEntry?)
    fun selectItem(value: ExploreMedia?)
    fun nextDay()
    fun setOnMyList(value: Boolean?)
}