package com.axiel7.anihyou.core.database.customlinks

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomLinksDao {

    @Insert
    suspend fun insertLink(link: CustomLinkEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLinks(links: List<CustomLinkEntity>): List<Long>

    @Upsert
    suspend fun upsertLink(link: CustomLinkEntity): Long

    @Query("SELECT * FROM custom_links WHERE mediaType = :mediaType")
    fun getAllLinks(mediaType: String): Flow<List<CustomLinkEntity>>

    @Delete
    suspend fun deleteLink(link: CustomLinkEntity): Int

    @Query("DELETE FROM custom_links WHERE id = :id")
    suspend fun deleteLinkById(id: Int): Int
}