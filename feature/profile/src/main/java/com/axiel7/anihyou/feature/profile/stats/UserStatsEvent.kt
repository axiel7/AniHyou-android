package com.axiel7.anihyou.feature.profile.stats

import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.model.stats.StatDistributionType
import com.axiel7.anihyou.core.network.type.MediaType

interface UserStatsEvent : UiEvent {
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
    fun onRefresh()
}