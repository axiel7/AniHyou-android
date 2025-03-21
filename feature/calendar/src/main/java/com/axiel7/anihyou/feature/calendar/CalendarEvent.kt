package com.axiel7.anihyou.feature.calendar

import com.axiel7.anihyou.core.network.AiringAnimesQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.ui.common.event.PagedEvent

interface CalendarEvent : PagedEvent {
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)

    fun selectItem(value: AiringAnimesQuery.AiringSchedule?)
}