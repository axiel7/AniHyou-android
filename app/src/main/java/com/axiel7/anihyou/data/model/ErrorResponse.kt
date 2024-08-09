package com.axiel7.anihyou.data.model

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
