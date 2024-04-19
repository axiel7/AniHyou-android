package com.axiel7.anihyou.ui.common.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.navArgument
import com.axiel7.anihyou.ui.common.navigation.TriBoolean.Companion.toBoolean
import com.axiel7.anihyou.ui.common.navigation.TriBoolean.Companion.toTriBoolean

@Immutable
data class DestArgument(
    val argument: NavArgument,
    val isNullable: Boolean = false,
    val defaultValue: Any? = null,
) {
    fun toNamedNavArgument() = navArgument(argument.name) {
        type = argument.type.toNavType()
        nullable = isNullable
        if (this@DestArgument.defaultValue != null) {
            defaultValue = this@DestArgument.defaultValue
        }
    }

    companion object {
        fun NavBackStackEntry.getStringArg(destArgument: DestArgument?) =
            if (destArgument?.argument?.type == ArgumentType.String)
                arguments?.getString(
                    destArgument.argument.name,
                    destArgument.defaultValue as? String?
                )
            else null

        fun NavBackStackEntry.getIntArg(destArgument: DestArgument?) =
            if (destArgument?.argument?.type == ArgumentType.Int)
                if (destArgument.defaultValue is Int)
                    arguments?.getInt(destArgument.argument.name, destArgument.defaultValue)
                else arguments?.getInt(destArgument.argument.name)
            else null

        fun NavBackStackEntry.getBoolean(destArgument: DestArgument?) =
            if (destArgument?.argument?.type == ArgumentType.Boolean) {
                if (destArgument.defaultValue is Boolean)
                    arguments?.getBoolean(destArgument.argument.name, destArgument.defaultValue)
                else arguments?.getBoolean(destArgument.argument.name)
            } else if (destArgument?.argument?.type == ArgumentType.BooleanOptional) {
                if (destArgument.defaultValue is Int) {
                    arguments?.getInt(
                        destArgument.argument.name,
                        destArgument.defaultValue
                    )?.toTriBoolean()?.toBoolean()
                } else arguments?.getInt(destArgument.argument.name)?.toTriBoolean()?.toBoolean()
            } else null
    }
}