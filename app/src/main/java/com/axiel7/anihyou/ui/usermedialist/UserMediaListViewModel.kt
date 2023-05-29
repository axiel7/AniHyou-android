package com.axiel7.anihyou.ui.usermedialist

import androidx.compose.runtime.mutableStateListOf
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.App
import com.axiel7.anihyou.UpdateEntryProgressMutation
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel

class UserMediaListViewModel(
    private val mediaType: MediaType
) : BaseViewModel() {

    var status = MediaListStatus.CURRENT

    var page = 1
    var hasNextPage = false
    var mediaList = mutableStateListOf<UserMediaListQuery.MediaList>()

    var sort = MediaListSort.safeValueOf(
        if (mediaType == MediaType.ANIME) App.animeListSort else App.mangaListSort
    )

    suspend fun getUserList() {
        isLoading = page == 1
        val userId = LoginRepository.getUserId()
        val response = UserMediaListQuery(
            page = Optional.present(page),
            perPage = Optional.present(15),
            userId = Optional.present(userId),
            type = Optional.present(mediaType),
            status = Optional.present(status),
            sort = Optional.present(listOf(sort, MediaListSort.MEDIA_ID_DESC))
        ).tryQuery()

        response?.data?.Page?.mediaList?.filterNotNull()?.let { mediaList.addAll(it) }
        hasNextPage = response?.data?.Page?.pageInfo?.hasNextPage ?: false
        page = response?.data?.Page?.pageInfo?.currentPage?.plus(1) ?: page
        isLoading = false
    }

    suspend fun refreshList() {
        hasNextPage = false
        page = 1
        mediaList.clear()
        getUserList()
    }

    suspend fun updateEntryProgress(entryId: Int, progress: Int) {
        isLoading = true
        val response = UpdateEntryProgressMutation(
            saveMediaListEntryId = Optional.present(entryId),
            progress = Optional.present(progress)
        ).tryMutation()

        if (response != null) {
            val foundIndex = mediaList.indexOfFirst { it.basicMediaListEntry.id == entryId }
            if (foundIndex != -1) mediaList[foundIndex] = mediaList[foundIndex].copy(
                basicMediaListEntry = mediaList[foundIndex].basicMediaListEntry.copy(progress = progress)
            )
        }

        isLoading = false
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