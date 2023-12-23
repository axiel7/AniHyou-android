package com.axiel7.anihyou.ui.screens.profile.stats

import com.axiel7.anihyou.data.model.stats.StatDistributionType
import com.axiel7.anihyou.type.MediaType

interface UserStatsEvent {
    fun setType(value: UserStatType)
    fun setMediaType(value: MediaType)
    fun setScoreType(value: StatDistributionType)
    fun setLengthType(value: StatDistributionType)
    fun setReleaseYearType(value: StatDistributionType)
    fun setStartYearType(value: StatDistributionType)
    fun setGenresType(value: StatDistributionType)
    fun setTagsType(value: StatDistributionType)
    fun setStaffType(value: StatDistributionType)
    fun setVoiceActorsType(value: StatDistributionType)
    fun setStudiosType(value: StatDistributionType)

}