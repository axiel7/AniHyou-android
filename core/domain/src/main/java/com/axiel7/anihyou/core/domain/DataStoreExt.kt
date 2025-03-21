package com.axiel7.anihyou.core.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

fun <T> DataStore<Preferences>.getValue(key: Preferences.Key<T>) = data.map { it[key] }

fun <T> DataStore<Preferences>.getValue(
    key: Preferences.Key<T>,
    default: T,
) = data.map { it[key] ?: default }

suspend fun <T> DataStore<Preferences>.setValue(
    key: Preferences.Key<T>,
    value: T?
) = edit {
    if (value != null) it[key] = value
    else it.remove(key)
}

fun <T> DataStore<Preferences>.getValueBlocking(key: Preferences.Key<T>) =
    runBlocking { data.first() }[key]