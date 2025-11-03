package com.axiel7.anihyou.feature.explore.charts

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.network.MediaChartQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.base.event.PagedEvent

@Immutable
interface MediaChartEvent : PagedEvent {
    fun selectItem(value: MediaChartQuery.Medium?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}