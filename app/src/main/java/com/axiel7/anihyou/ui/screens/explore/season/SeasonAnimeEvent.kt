package com.axiel7.anihyou.ui.screens.explore.season

import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.event.PagedEvent

interface SeasonAnimeEvent : PagedEvent {
    fun setSeason(value: AnimeSeason)
    fun selectItem(value: SeasonalAnimeQuery.Medium?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
    fun onChangeListStyle(value: ListStyle)
}