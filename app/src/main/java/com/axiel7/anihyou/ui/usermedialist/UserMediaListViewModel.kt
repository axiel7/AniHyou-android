package com.axiel7.anihyou.ui.usermedialist

import androidx.compose.runtime.mutableStateListOf
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.App
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
        isLoading = true
        val userId = LoginRepository.getUserId()
        val response = UserMediaListQuery(
            page = Optional.present(page),
            perPage = Optional.present(15),
            userId = Optional.present(userId),
            type = Optional.present(mediaType),
            status = Optional.present(status),
            sort = Optional.present(listOf(sort))
        ).tryQuery()

        response?.data?.Page?.mediaList?.filterNotNull()?.let { mediaList.addAll(it) }
        page += 1
        hasNextPage = response?.data?.Page?.pageInfo?.hasNextPage ?: false
        isLoading = false
    }

    suspend fun refreshList() {
        page = 1
        hasNextPage = true
        mediaList.clear()
        getUserList()
    }
}