package com.axiel7.anihyou.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.axiel7.anihyou.data.api.MediaApi
import com.axiel7.anihyou.data.model.GenresAndTags
import com.axiel7.anihyou.data.model.SelectableGenre
import com.axiel7.anihyou.data.paging.SearchMediaPagingSourceFactory
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val mediaApi: MediaApi,
    private val searchMediaPagingSourceFactory: SearchMediaPagingSourceFactory
) {

    fun searchMedia(
        mediaType: MediaType,
        query: String,
        sort: List<MediaSort> = listOf(MediaSort.SEARCH_MATCH),
        genreIn: List<String>? = null,
        tagIn: List<String>? = null,
        formatIn: List<MediaFormat>? = null,
        statusIn: List<MediaStatus>? = null,
        year: Int? = null,
        onList: Boolean? = null,
    ) = Pager(
        config = PagingConfig(pageSize = 25)
    ) {
        searchMediaPagingSourceFactory.create(
            mediaType,
            query,
            sort,
            genreIn,
            tagIn,
            formatIn,
            statusIn,
            year,
            onList
        )
    }

    fun searchCharacter(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) {
        TODO("use pagination")
    }

    fun searchStaff(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) {
        TODO("use pagination")
    }

    fun searchStudio(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) {
        TODO("use pagination")
    }

    fun searchUser(
        query: String,
        page: Int = 1,
        perPage: Int = 25,
    ) {
        TODO("use pagination")
    }

    fun getGenreTagCollection() = mediaApi
        .genreTagCollectionQuery()
        .toFlow()
        .asDataResult { data ->
            GenresAndTags(
                genres = data.GenreCollection?.filterNotNull().orEmpty()
                    .map { SelectableGenre(it, false) },
                tags = data.MediaTagCollection?.filterNotNull()?.map { it.name }.orEmpty()
                    .map { SelectableGenre(it, false) }
            )
        }
}