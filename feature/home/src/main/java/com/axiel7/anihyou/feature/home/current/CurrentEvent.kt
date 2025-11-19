package com.axiel7.anihyou.feature.home.current

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.model.CurrentListType
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry

@Immutable
interface CurrentEvent : UiEvent {
    fun refresh()

    fun onClickPlusOne(increment: Int, item: CommonMediaListEntry, type: CurrentListType)

    fun blockPlusOne()

    fun onUpdateListEntry(
        newListEntry: BasicMediaListEntry?,
        type: CurrentListType
    )

    fun selectItem(item: CommonMediaListEntry, type: CurrentListType)

    fun toggleSetScoreDialog(open: Boolean)

    fun setScore(score: Double?)
}