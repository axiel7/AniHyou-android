package com.axiel7.anihyou.ui.screens.usermedialist

import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.event.PagedEvent
import com.axiel7.anihyou.ui.common.event.UiEvent

interface UserMediaListEvent : PagedEvent, UiEvent {
    fun setScoreFormat(value: ScoreFormat)

    fun onChangeList(listName: String?)

    fun setSort(value: MediaListSort)

    fun toggleSortMenu(open: Boolean)

    fun toggleNotesDialog(open: Boolean)

    fun onClickNotes(value: CommonMediaListEntry?) {
        selectItem(value)
        toggleNotesDialog(true)
    }

    fun refreshList()

    fun onClickPlusOne(entry: CommonMediaListEntry)

    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)

    fun selectItem(value: CommonMediaListEntry?)

    fun setScore(score: Double?)

    fun toggleScoreDialog(open: Boolean)

    fun getRandomPlannedEntry(chunk: Int = 1)

    fun onRandomEntryOpened()
}