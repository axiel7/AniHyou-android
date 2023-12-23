package com.axiel7.anihyou.ui.screens.mediadetails.edit

import com.axiel7.anihyou.type.MediaListStatus

interface EditMediaEvent {
    fun onChangeStatus(value: MediaListStatus)
    fun onChangeProgress(value: Int?)
    fun onChangeVolumeProgress(value: Int?)
    fun onChangeScore(value: Double)
    fun setStartedAt(value: Long?)
    fun setCompletedAt(value: Long?)
    fun onDateDialogOpen(dateType: Int)
    fun onDateDialogClosed()
    fun onChangeRepeatCount(value: Int?): Boolean
    fun setIsPrivate(value: Boolean)
    fun setNotes(value: String)
    fun updateListEntry()
    fun updateCustomLists(customsList: List<String>)
    fun getCustomLists()
    fun toggleCustomListsDialog(open: Boolean)
    fun toggleDeleteDialog(open: Boolean)
    fun deleteListEntry()
    fun setUpdateSuccess(value: Boolean)

    fun onErrorDisplayed()
}