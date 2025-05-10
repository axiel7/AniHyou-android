package com.axiel7.anihyou.core.model.media

import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaType

data class ListType(
    val status: MediaListStatus,
    val mediaType: MediaType,
)
