package com.axiel7.anihyou.feature.usermedialist

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.network.type.MediaStatus
import com.axiel7.anihyou.core.network.type.ScoreFormat

@Immutable
interface UserMediaListEvent : UiEvent {
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

    fun onClickPlusOne(increment: Int, entry: CommonMediaListEntry)

    fun blockPlusOne()

    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)

    fun selectItem(value: CommonMediaListEntry?)

    fun setScore(score: Double?)

    fun toggleScoreDialog(open: Boolean)

    fun getRandomEntry()

    fun onRandomEntryOpened()

    fun onSearch(query: String)

    fun setMediaFormat(value: MediaFormatLocalizable?)

    fun setMediaStatus(value: MediaStatus?)

    fun setCountry(value: CountryOfOrigin?)

    fun setYear(value: Int?)

    fun clearFilters()
}