package com.axiel7.anihyou.feature.explore.season

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.ExploreMedia
import com.axiel7.anihyou.core.network.type.MediaSort

@Immutable
interface SeasonAnimeEvent : UiEvent, PagedEvent {
    fun setSeason(value: AnimeSeason)
    fun onChangeSort(value: MediaSort)
    fun selectItem(value: ExploreMedia?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
    fun onChangeListStyle(value: ListStyle)
}