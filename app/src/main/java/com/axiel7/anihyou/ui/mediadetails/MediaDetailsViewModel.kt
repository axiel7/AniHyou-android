package com.axiel7.anihyou.ui.mediadetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.MediaCharactersAndStaffQuery
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
        if (mediaDetails != null) isLoading = false
    }

    fun getStudios() = mediaDetails?.studios?.nodes?.filterNotNull()?.filter { it.isAnimationStudio }

    fun getProducers() = mediaDetails?.studios?.nodes?.filterNotNull()?.filter { !it.isAnimationStudio }

    var mediaStaff = mutableStateListOf<MediaCharactersAndStaffQuery.Edge1>()
    var mediaCharacters = mutableStateListOf<MediaCharactersAndStaffQuery.Edge>()

    suspend fun getMediaCharactersAndStaff(mediaId: Int) {
        val response = MediaCharactersAndStaffQuery(
            mediaId = Optional.present(mediaId)
        ).tryQuery()

        mediaStaff.clear()
        response?.data?.Media?.staff?.edges?.filterNotNull()?.let { mediaStaff.addAll(it) }

        mediaCharacters.clear()
        response?.data?.Media?.characters?.edges?.filterNotNull()?.let { mediaCharacters.addAll(it) }
    }
}