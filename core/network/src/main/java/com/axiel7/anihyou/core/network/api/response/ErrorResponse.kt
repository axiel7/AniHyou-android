package com.axiel7.anihyou.core.network.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val errors: List<Error>
) {
    @Serializable
    data class Error(
        val message: String,
    )
}