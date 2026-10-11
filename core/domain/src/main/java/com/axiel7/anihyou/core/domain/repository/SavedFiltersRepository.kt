package com.axiel7.anihyou.core.domain.repository

import com.axiel7.anihyou.core.database.savedfilters.SavedFiltersDao
import com.axiel7.anihyou.core.domain.model.SavedFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SavedFiltersRepository(
    private val dao: SavedFiltersDao
) {
    suspend fun insertFilter(filter: SavedFilter) = withContext(Dispatchers.IO) {
        dao.insertFilter(filter.toEntity())
    }

    suspend fun getAllFilters() = withContext(Dispatchers.IO) {
        dao.getAllFilters().map { list -> list.map { SavedFilter.fromEntity(it) } }
    }

    suspend fun deleteFilter(id: Int) = withContext(Dispatchers.IO) {
        dao.deleteFilter(id)
    }
}