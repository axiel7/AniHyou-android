package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.App
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.repository.MediaListRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.ui.base.UiState
import kotlinx.coroutines.launch

class UserMediaListViewModel(
    val mediaType: MediaType,
    val userId: Int?,
) : BaseViewModel() {

    var status by mutableStateOf(MediaListStatus.CURRENT)
    private set

    fun onStatusChanged(status: MediaListStatus) {
        this.status = status
        viewModelScope.launch { refreshList(refreshCache = false) }
    }

    var page = 1
    var hasNextPage = true
    var mediaList = mutableStateListOf<UserMediaListQuery.MediaList>()

    var sort = MediaListSort.safeValueOf(
        if (mediaType == MediaType.ANIME) App.animeListSort else App.mangaListSort
    )
    private set

    fun onSortChanged(sort: MediaListSort) {
        this.sort = sort
        viewModelScope.launch {
            App.dataStore.edit {
                if (mediaType == MediaType.ANIME)
                    it[ANIME_LIST_SORT_PREFERENCE_KEY] = sort.rawValue
                else
                    it[MANGA_LIST_SORT_PREFERENCE_KEY] = sort.rawValue
            }

            refreshList(refreshCache = false)
        }
    }

    suspend fun getUserMediaList(
        refreshCache: Boolean = false
    ) = viewModelScope.launch {
        MediaListRepository.getUserMediaListPage(
            userId = userId,
            mediaType = mediaType,
            status = status,
            sort = sort,
            refreshCache = refreshCache,
            page = page
        ).collect { uiState ->
            isLoading = page == 1 && uiState is UiState.Loading

            if (uiState is UiState.Success) {
                uiState.data.mediaList?.filterNotNull()?.let { mediaList.addAll(it) }
                hasNextPage = uiState.data.pageInfo?.hasNextPage ?: false
                page = uiState.data.pageInfo?.currentPage?.plus(1) ?: page++
            }
            else if (uiState is UiState.Error) {
                message = uiState.message
            }
        }
    }


    suspend fun refreshList(
        refreshCache: Boolean
    ) {
        hasNextPage = false
        page = 1
        mediaList.clear()
        getUserMediaList(refreshCache)
    }

    suspend fun updateEntryProgress(
        entryId: Int,
        progress: Int
    ) = viewModelScope.launch {
        MediaListRepository.updateEntryProgress(
            entryId = entryId,
            progress = progress,
        ).collect { uiState ->
            isLoading = uiState is UiState.Loading

            if (uiState is UiState.Success) {
                val foundIndex = mediaList.indexOfFirst { it.basicMediaListEntry.id == entryId }
                if (foundIndex != -1) mediaList[foundIndex] = mediaList[foundIndex].copy(
                    basicMediaListEntry = mediaList[foundIndex].basicMediaListEntry.copy(progress = progress)
                )
            }
        }
    }

    var selectedItem: UserMediaListQuery.MediaList? = null

    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        if (selectedItem != null && selectedItem?.basicMediaListEntry != newListEntry) {
            if (newListEntry != null) {
                val index = mediaList.indexOf(selectedItem)
                if (index != -1) {
                    mediaList[index] = selectedItem!!.copy(basicMediaListEntry = newListEntry)
                }
            } else {
                mediaList.remove(selectedItem)
            }
        }
    }
}