package com.axiel7.anihyou.core.model.staff

import com.axiel7.anihyou.core.network.StaffMediaQuery

data class StaffMediaGrouped(
    val value: StaffMediaQuery.Edge,
    val staffRoles: List<String>,
)
