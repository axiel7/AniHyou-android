package com.axiel7.anihyou.ui.usermedialist

import androidx.compose.runtime.mutableStateListOf
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.network.apolloClient
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel

class UserMediaListViewModel : BaseViewModel() {

    var page = 1
    var hasNextPage = true
    var mediaList = mutableStateListOf<UserMediaListQuery.MediaList>()

    suspend fun getUserList() {
        val response = UserMediaListQuery(
            page = Optional.present(page),
            perPage = Optional.present(15),
            userId = Optional.present(208863),
            type = Optional.present(MediaType.ANIME),
            status = Optional.present(MediaListStatus.CURRENT),
            sort = Optional.present(listOf(MediaListSort.UPDATED_TIME_DESC))
        ).tryQuery()

        response?.data?.Page?.mediaList?.filterNotNull()?.let { mediaList.addAll(it) }
        page += 1
        hasNextPage = response?.data?.Page?.pageInfo?.hasNextPage ?: false
    }
}