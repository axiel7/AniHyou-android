package com.axiel7.anihyou.core.database.savedfilters

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedFiltersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilter(filter: SavedFilterEntity): Long

    @Query("SELECT * FROM saved_filters")
    fun getAllFilters(): Flow<List<SavedFilterEntity>>

    @Query("DELETE FROM saved_filters WHERE id = :id")
    suspend fun deleteFilter(id: Int): Int
}