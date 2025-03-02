package com.axiel7.anihyou.ui.screens.home.current

import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.CommonMediaListEntry

interface CurrentEvent {
    fun refresh()

    fun onClickPlusOne(item: CommonMediaListEntry, type: CurrentListType)

    fun onUpdateListEntry(
        newListEntry: BasicMediaListEntry?,
        type: CurrentListType
    )

    fun selectItem(item: CommonMediaListEntry, type: CurrentListType)

    fun toggleSetScoreDialog(open: Boolean)

    fun setScore(score: Double?)
}