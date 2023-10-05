package com.axiel7.anihyou.data.repository

import com.axiel7.anihyou.data.api.StudioApi
import javax.inject.Inject

class StudioRepository @Inject constructor(
    private val api: StudioApi
) {

    fun getStudioDetails(
        studioId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) {
        TODO("use pagination")
    }
}