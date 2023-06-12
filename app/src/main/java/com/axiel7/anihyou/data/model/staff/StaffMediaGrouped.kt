package com.axiel7.anihyou.data.model.staff

import com.axiel7.anihyou.StaffMediaQuery

data class StaffMediaGrouped(
    val value: StaffMediaQuery.Edge,
    val staffRoles: List<String>,
)
