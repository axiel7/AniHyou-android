package com.axiel7.anihyou.ui.screens.explore.charts

import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.common.event.PagedEvent

interface MediaChartEvent : PagedEvent {
    fun selectItem(value: MediaChartQuery.Medium?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}