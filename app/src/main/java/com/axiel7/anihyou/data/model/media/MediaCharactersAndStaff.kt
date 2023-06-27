package com.axiel7.anihyou.data.model.media

import com.axiel7.anihyou.MediaCharactersAndStaffQuery

data class MediaCharactersAndStaff(
    val characters: List<MediaCharactersAndStaffQuery.Edge>,
    val staff: List<MediaCharactersAndStaffQuery.Edge1>,
)
