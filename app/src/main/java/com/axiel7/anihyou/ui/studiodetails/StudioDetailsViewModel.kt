package com.axiel7.anihyou.ui.studiodetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.StudioDetailsQuery
import com.axiel7.anihyou.ui.base.BaseViewModel

class StudioDetailsViewModel : BaseViewModel() {

    var page = 1
    var hasNextPage = true
    var studioDetails by mutableStateOf<StudioDetailsQuery.Studio?>(null)
    val studioMedia = mutableStateListOf<StudioDetailsQuery.Node>()

    suspend fun getStudioDetails(studioId: Int) {
        isLoading = page == 1

        val response = StudioDetailsQuery(
            studioId = Optional.present(studioId),
            page = Optional.present(page),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.Studio?.let { studio ->
            studioDetails = studio
            studio.media?.nodes?.filterNotNull()?.let { studioMedia.addAll(it) }
            page = studio.media?.pageInfo?.currentPage?.plus(1) ?: page
            hasNextPage = studio.media?.pageInfo?.hasNextPage ?: false
        }

        isLoading = false
    }
}