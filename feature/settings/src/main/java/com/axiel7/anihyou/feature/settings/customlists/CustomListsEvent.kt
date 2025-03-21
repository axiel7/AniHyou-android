package com.axiel7.anihyou.feature.settings.customlists

import com.axiel7.anihyou.core.network.type.MediaType

interface CustomListsEvent {
    fun onListAdded(list: String, mediaType: MediaType)
    fun onListRemoved(list: String, mediaType: MediaType)
    fun updateCustomLists()
}