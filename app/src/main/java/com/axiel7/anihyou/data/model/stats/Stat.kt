package com.axiel7.anihyou.data.model.stats

import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.LocalizableAndColorable

abstract class Stat<T> {
    abstract val type: T
    abstract val value: Float
}

data class StatLocalizableAndColorable<T : LocalizableAndColorable>(
    override val type: T,
    override val value: Float,
) : Stat<T>()

data class StatColorable<T : Colorable>(
    override val type: T,
    override val value: Float,
) : Stat<T>()
