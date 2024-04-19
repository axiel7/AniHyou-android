package com.axiel7.anihyou.ui.common.navigation

import androidx.navigation.NavType

enum class ArgumentType {
    String, Int, Boolean, BooleanOptional;

    fun toNavType() = when (this) {
        String -> NavType.StringType
        Int -> NavType.IntType
        Boolean -> NavType.BoolType
        BooleanOptional -> NavType.IntType // null boolean is not supported in androidx.navigation
    }
}