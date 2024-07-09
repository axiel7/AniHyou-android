package com.axiel7.anihyou.ui.screens.home.current

import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.CommonMediaListEntry

interface CurrentEvent {
    fun refresh()

    fun onClickPlusOne(item: CommonMediaListEntry, type: CurrentUiState.Companion.ListType)

    fun onUpdateListEntry(
        newListEntry: BasicMediaListEntry?,
        type: CurrentUiState.Companion.ListType
    )

    fun selectItem(item: CommonMediaListEntry, type: CurrentUiState.Companion.ListType)

    fun toggleSetScoreDialog(open: Boolean)

    fun setScore(score: Double?)
}