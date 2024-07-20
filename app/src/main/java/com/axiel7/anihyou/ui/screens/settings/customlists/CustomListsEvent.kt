package com.axiel7.anihyou.ui.screens.settings.customlists

import com.axiel7.anihyou.type.MediaType

interface CustomListsEvent {
    fun onListAdded(list: String, mediaType: MediaType)
    fun onListRemoved(list: String, mediaType: MediaType)
    fun updateCustomLists()
}