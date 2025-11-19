package com.axiel7.anihyou.feature.explore.charts

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.network.MediaChartQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface MediaChartEvent : UiEvent, PagedEvent {
    fun selectItem(value: MediaChartQuery.Medium?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}