package com.axiel7.anihyou.ui.common.navigation

// this is required because androidx.navigation doesn't support nullable boolean
enum class TriBoolean(val value: Byte) {
    TRUE(1),
    FALSE(0),
    NONE(2);

    companion object {
        fun Int.toTriBoolean() = when (this) {
            0 -> FALSE
            1 -> TRUE
            else -> NONE
        }

        fun TriBoolean.toBoolean() = when (this) {
            TRUE -> true
            FALSE -> false
            else -> null
        }
    }
}