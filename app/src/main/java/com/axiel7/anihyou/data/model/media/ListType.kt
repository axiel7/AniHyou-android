package com.axiel7.anihyou.data.model.media

import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType

data class ListType(
    val status: MediaListStatus,
    val mediaType: MediaType,
)
