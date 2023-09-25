package com.axiel7.anihyou.data.model.stats

import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.Localizable

abstract class Stat<T> {
    abstract val type: T
    abstract val value: Float
}

data class StatLocalizableAndColorable<T>(
    override val type: T,
    override val value: Float,
) : Stat<T>() where T : Localizable, T : Colorable

data class StatColorable<T : Colorable>(
    override val type: T,
    override val value: Float,
) : Stat<T>()
