package com.axiel7.anihyou.feature.mediadetails.dubschedule

import com.axiel7.anihyou.core.base.event.UiEvent

interface DubScheduleEvent : UiEvent {
    fun loadDubSchedule(englishTitle: String?, romajiTitle: String?, season: Int = 1)
    fun changeSeason(season: Int)
}
