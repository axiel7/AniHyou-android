package com.axiel7.anihyou.data.model.stats

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.Localizable

abstract class Stat<T> {
    abstract val type: T
    abstract val value: Float
    abstract val details: List<Detail>?

    data class Detail(
        @StringRes
        /**
         * This should be a string resource with one `%s`
         * so [text] can be formatted with the [value] argument
         */
        val name: Int,
        val value: String
    ) {
        @Composable
        fun text() = stringResource(id = name, value)
    }
}

data class StatLocalizableAndColorable<T>(
    override val type: T,
    override val value: Float,
    override val details: List<Detail>? = null,
) : Stat<T>() where T : Localizable, T : Colorable

data class StatColorable<T : Colorable>(
    override val type: T,
    override val value: Float,
    override val details: List<Detail>? = null,
) : Stat<T>()
