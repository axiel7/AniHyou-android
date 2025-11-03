package com.axiel7.anihyou.feature.calendar

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.network.AiringAnimesQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.base.event.PagedEvent

@Immutable
interface CalendarEvent : PagedEvent {
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)

    fun selectItem(value: AiringAnimesQuery.AiringSchedule?)
}