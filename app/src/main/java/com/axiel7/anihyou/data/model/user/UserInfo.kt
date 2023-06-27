package com.axiel7.anihyou.data.model.user

import androidx.compose.runtime.Composable
import com.axiel7.anihyou.fragment.UserInfo

@Composable
fun UserInfo.hexColor() =
    if (options?.profileColor?.startsWith('#') == true) options.profileColor
    else when (this.options?.profileColor) {
        "blue" -> "#3DB4F2"
        "purple" -> "#C063FF"
        "pink" -> "#FC9DD6"
        "orange" -> "#EF881B"
        "red" -> "#E13433"
        "green" -> "#4DCA51"
        "gray" -> "#677B94"
        else -> "#3DB4F2"
}