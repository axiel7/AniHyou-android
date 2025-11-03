package com.axiel7.anihyou.feature.settings.customlists

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.network.type.MediaType

@Immutable
interface CustomListsEvent {
    fun onListAdded(list: String, mediaType: MediaType)
    fun onListRemoved(list: String, mediaType: MediaType)
    fun updateCustomLists()
}