package com.axiel7.anihyou.ui.screens.calendar

import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.common.event.PagedEvent

interface CalendarEvent : PagedEvent {
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)

    fun selectItem(value: AiringAnimesQuery.AiringSchedule?)
}