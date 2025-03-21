package com.axiel7.anihyou.core.model.media

import com.axiel7.anihyou.core.network.MediaCharactersAndStaffQuery

data class MediaCharactersAndStaff(
    val characters: List<MediaCharactersAndStaffQuery.Edge>,
    val staff: List<MediaCharactersAndStaffQuery.Edge1>,
)
