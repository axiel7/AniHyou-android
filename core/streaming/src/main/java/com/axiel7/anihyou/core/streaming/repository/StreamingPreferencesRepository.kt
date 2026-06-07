package com.axiel7.anihyou.core.streaming.repository

import com.axiel7.anihyou.core.streaming.model.StreamingSource
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory cache for source-specific anime IDs.
 * (We avoid writing to DataStore here to keep the module a pure JVM lib.)
 * The cache lives as long as the app process — good enough.
 */
class StreamingPreferencesRepository {

    private val cache = ConcurrentHashMap<String, String>()

    fun getAnimeSourceId(anilistId: Int, source: StreamingSource): String? =
        cache["$anilistId:${source.name}"]

    fun saveAnimeSourceId(anilistId: Int, source: StreamingSource, sourceId: String) {
        cache["$anilistId:${source.name}"] = sourceId
    }

    // User-selected streaming source preference (persisted via DataStore in the app layer)
    private var _selectedSource = StreamingSource.ALL_ANIME
    var selectedSource: StreamingSource
        get() = _selectedSource
        set(value) { _selectedSource = value }
}
