package com.axiel7.anihyou.ui.usermedialist

import androidx.compose.runtime.mutableStateListOf
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.App
import com.axiel7.anihyou.UpdateEntryProgressMutation
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel

class UserMediaListViewModel(
    private val mediaType: MediaType,
    private val status: MediaListStatus
) : BaseViewModel() {

    var page = 1
    var hasNextPage = true
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

        response?.data?.Page?.mediaList?.filterNotNull()?.let {
            // Workaround: AniList API bug that always returns incorrect status
            // when the order is set to title
            if (sort == MediaListSort.MEDIA_TITLE_ROMAJI) {
                it.forEach { item ->
                    mediaList.add(item.copy(
                        basicMediaListEntry = item.basicMediaListEntry.copy(status = status)
                    ))
                }
            } else mediaList.addAll(it)
        }
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
}