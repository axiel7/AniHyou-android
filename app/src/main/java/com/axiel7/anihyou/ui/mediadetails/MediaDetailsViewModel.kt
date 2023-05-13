package com.axiel7.anihyou.ui.mediadetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.ui.base.BaseViewModel

class MediaDetailsViewModel : BaseViewModel() {

    var mediaDetails by mutableStateOf<MediaDetailsQuery.Media?>(null)

    suspend fun getDetails(mediaId: Int) {
        isLoading = true
        val response = MediaDetailsQuery(
            mediaId = Optional.present(mediaId)
        ).tryQuery()

        mediaDetails = response?.data?.Media
        isLoading = false
    }

}