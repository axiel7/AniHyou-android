package com.axiel7.anihyou.core.domain.repository

import com.axiel7.anihyou.core.database.customlinks.CustomLinksDao
import com.axiel7.anihyou.core.domain.model.CustomLink
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CustomLinksRepository(
    private val dao: CustomLinksDao
) {
    suspend fun insertLinks(links: List<CustomLink>) = withContext(Dispatchers.IO) {
        dao.insertLinks(links.map { it.toEntity() })
    }

    suspend fun upsertCustomLink(link: CustomLink) = withContext(Dispatchers.IO) {
        dao.upsertLink(link.toEntity())
    }

    suspend fun getAllCustomLinks(mediaType: MediaType) = withContext(Dispatchers.IO) {
        dao.getAllLinks(mediaType.name)
            .map { list -> list.map { CustomLink.fromEntity(it) } }
    }

    suspend fun deleteCustomLink(link: CustomLink) = withContext(Dispatchers.IO) {
        dao.deleteLink(link.toEntity())
    }
}