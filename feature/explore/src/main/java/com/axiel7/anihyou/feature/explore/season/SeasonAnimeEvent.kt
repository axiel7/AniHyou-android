package com.axiel7.anihyou.feature.explore.season

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.network.SeasonalAnimeQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.base.event.PagedEvent

@Immutable
interface SeasonAnimeEvent : PagedEvent {
    fun setSeason(value: AnimeSeason)
    fun onChangeSort(value: MediaSort)
    fun selectItem(value: SeasonalAnimeQuery.Medium?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
    fun onChangeListStyle(value: ListStyle)
}