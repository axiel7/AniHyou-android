package com.axiel7.anihyou.core.base.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun <T> Flow<T>.firstBlocking() = runBlocking { first() }