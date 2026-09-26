package com.axiel7.anihyou.feature.calendar

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.ExploreMedia

import java.time.LocalDate

@Immutable
interface CalendarEvent : UiEvent, PagedEvent {
    fun onMyListChanged(value: Boolean?)
    fun onChangeListStyle(value: ListStyle)
    fun onUpdateListEntry(viewListEntry: BasicMediaListEntry?)
    fun selectItem(value: ExploreMedia?)
    fun nextDay()
    fun refresh()
    fun refreshDay(date: LocalDate)
    fun onAutoScrolled()
}