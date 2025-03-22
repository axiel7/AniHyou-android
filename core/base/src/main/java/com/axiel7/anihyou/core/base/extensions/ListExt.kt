package com.axiel7.anihyou.core.base.extensions

/**
 * Returns index of the first element matching the given [predicate], or `null` if the list does not contain such element.
 */
fun <T> List<T>.indexOfFirstOrNull(predicate: (T) -> Boolean) =
    indexOfFirst(predicate).takeIf { it != -1 }