package com.axiel7.anihyou.ui.screens.usermedialist

import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.event.PagedEvent

interface UserMediaListEvent : PagedEvent {
    fun setScoreFormat(value: ScoreFormat)

    fun setStatus(value: MediaListStatus?)

    fun setSort(value: MediaListSort)

    fun toggleSortMenu(open: Boolean)

    fun toggleNotesDialog(open: Boolean)

    fun onClickNotes(value: CommonMediaListEntry?) {
        selectItem(value)
        toggleNotesDialog(true)
    }

    fun refreshList()

    fun updateEntryProgress(entryId: Int, progress: Int)

    fun onClickPlusOne(entry: BasicMediaListEntry) {
        updateEntryProgress(
            entryId = entry.id,
            progress = (entry.progress ?: 0) + 1
        )
    }

    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)

    fun selectItem(value: CommonMediaListEntry?)
}